package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import com.infobank.multiagentplatform.orchestrator.exception.AgentInactiveException;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExecutionPlanExecutor {

    private final BrokerClient brokerClient;
    private final TaskBlockExecutor blockExecutor;
    private final ReactiveMetricOperator metricOperator;
    private final Duration planTimeout;

    public ExecutionPlanExecutor(BrokerClient brokerClient,
                                 TaskBlockExecutor blockExecutor,
                                 ReactiveMetricOperator metricOperator,
                                 @Value("${orchestrator.timeouts.plan-operator:2s}") Duration planTimeout) {
        this.brokerClient = brokerClient;
        this.blockExecutor = blockExecutor;
        this.metricOperator = metricOperator;
        this.planTimeout = planTimeout;
    }

    @CircuitBreaker(name = "executorCB", fallbackMethod = "fallbackExecutePlanReactive")
    public Mono<Map<String, TaskResult>> executePlanReactive(Mono<ExecutionPlan> planMono) {

        Mono<Map<String, TaskResult>> executionMono = planMono.flatMap(plan -> {
            List<String> agentIds = plan.getBlocks().stream()
                    .flatMap(b -> b.getTasks().stream().map(AgentTask::getAgentId))
                    .distinct()
                    .collect(Collectors.toList());

            Mono<Map<String, AgentDetailResponse>> metadataMono = brokerClient.getAgentMetadataBatch(agentIds)
                    .flatMapMany(Flux::fromIterable)
                    .collectMap(AgentDetailResponse::getUuid, Function.identity())
                    .transform(metricOperator.measure("orchestration.executor.metadata"));

            return metadataMono.flatMap(metadataMap -> {
                var inactiveAgent = metadataMap.values().stream()
                        .filter(meta -> meta.getStatus() != AgentStatus.ACTIVE)
                        .findFirst();

                if (inactiveAgent.isPresent()) {
                    var meta = inactiveAgent.get();
                    throw new AgentInactiveException("Agent is inactive: " + meta.getUuid());
                }

                Mono<Map<String, TaskResult>> blocksMono = Flux.fromIterable(plan.getBlocks())
                        .flatMap(block -> blockExecutor.executeBlockReactive(
                                block, metadataMap, new ConcurrentHashMap<>()
                        ))
                        .collectMap(TaskResult::getTaskId, Function.identity())
                        .transform(metricOperator.measure("orchestration.executor.blocks"));

                return blocksMono;
            })
            .timeout(planTimeout);
        });

        return executionMono
                .transform(metricOperator.measure("orchestration.executor"));
    }

    private Mono<Map<String, TaskResult>> fallbackExecutePlanReactive(
            Mono<ExecutionPlan> planMono, Throwable ex) {
        return planMono.flatMap(plan -> {
            Map<String, TaskResult> fallback = plan.getBlocks().stream()
                    .flatMap(block -> block.getTasks().stream())
                    .collect(Collectors.toMap(
                            AgentTask::getId,
                            task -> TaskResult.of(
                                    task.getId(), null,
                                    Map.of("fallback", "execution_error", "reason", ex.getMessage())
                            )
                    ));
            return Mono.just(fallback);
        });
    }
}
