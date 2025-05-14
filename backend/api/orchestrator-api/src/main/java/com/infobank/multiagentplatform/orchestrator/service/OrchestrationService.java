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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final BrokerClient brokerClient;
    private final TaskPlanner planner;
    private final ExecutionPlanExecutor executor;
    private final ResultPostProcessor postProcessor;

    public Mono<OrchestrationResponse> orchestrate(OrchestrationServiceRequest request) {
        Mono<List<AgentSummaryResponse>> agents = brokerClient.getAgentSummaries()
                .flatMap(list -> list.isEmpty()
                        ? Mono.error(new AgentInactiveException("활성화된 에이전트가 없습니다."))
                        : Mono.just(list)
                );

        Mono<Map<String, TaskResult>> rawResult = planner.plan(request, agents)
                .flatMap(plan -> executor.executePlanReactive(Mono.just(plan)))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2))
                        .filter(ex -> ex instanceof AgentInactiveException)
                );

        return postProcessor.process(rawResult)
                .onErrorResume(AgentInactiveException.class, ex ->
                        Mono.error(new IllegalStateException(
                                "에이전트 상태 불일치로 작업을 재시도했습니다. " + ex.getMessage(), ex
                        ))
                );
    }
}