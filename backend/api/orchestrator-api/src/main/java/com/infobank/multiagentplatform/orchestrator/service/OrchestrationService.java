package com.infobank.multiagentplatform.orchestrator.service;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.exception.AgentInactiveException;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.executor.ExecutionPlanExecutor;
import com.infobank.multiagentplatform.orchestrator.service.planner.TaskPlanner;
import com.infobank.multiagentplatform.orchestrator.service.postprocessor.ResultPostProcessor;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrchestrationService {

    private final BrokerClient brokerClient;
    private final TaskPlanner planner;
    private final ExecutionPlanExecutor executor;
    private final ResultPostProcessor postProcessor;
    private final ReactiveMetricOperator metricOperator;

    public OrchestrationService(BrokerClient brokerClient, TaskPlanner planner,
                                ExecutionPlanExecutor executor, ResultPostProcessor postProcessor,
                                ReactiveMetricOperator metricOperator) {
        this.brokerClient = brokerClient;
        this.planner = planner;
        this.executor = executor;
        this.postProcessor = postProcessor;
        this.metricOperator = metricOperator;
    }

    public Flux<String> orchestrate(OrchestrationServiceRequest request) {
        Mono<List<AgentSummaryResponse>> agents = brokerClient.getAgentSummaries()
                .flatMap(list -> list.isEmpty()
                        ? Mono.error(new AgentInactiveException("활성화된 에이전트가 없습니다."))
                        : Mono.just(list)
                );

        Mono<Map<String, TaskResult>> rawResult = planner.plan(request, agents)
                .flatMap(plan -> executor.executePlanReactive(Mono.just(plan)));

        return rawResult
                .flatMapMany(results ->
                        postProcessor.process(Mono.just(results))
                )
                .onErrorResume(AgentInactiveException.class, ex -> {
                    log.error("에이전트 상태 불일치 발생: {}", ex.getMessage());
                    return Flux.error(new IllegalStateException(
                            "에이전트 상태 불일치: " + ex.getMessage(), ex
                    ));
                })
                .doOnError(error -> log.error("=== 오케스트레이션 실패 ===: {}", error.getMessage(), error));
    }
}