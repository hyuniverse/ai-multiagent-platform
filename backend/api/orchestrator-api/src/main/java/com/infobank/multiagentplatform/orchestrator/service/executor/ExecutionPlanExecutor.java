package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import com.infobank.multiagentplatform.orchestrator.exception.AgentInactiveException;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ExecutionPlanExecutor {

    private final BrokerClient brokerClient;
    private final TaskBlockExecutor blockExecutor;

    @CircuitBreaker(name = "executorCircuit", fallbackMethod = "fallbackExecutePlanReactive")
    @Retry(name = "executorRetry")
    public Mono<Map<String, TaskResult>> executePlanReactive(Mono<ExecutionPlan> planMono) {
        return planMono.flatMap(plan -> {
            List<String> agentIds = plan.getBlocks().stream()
                    .flatMap(b -> b.getTasks().stream().map(AgentTask::getAgentId))
                    .distinct()
                    .collect(Collectors.toList());

            // 2) metadataMap 조회
            return brokerClient.getAgentMetadataBatch(agentIds)
                    .flatMapMany(Flux::fromIterable)
                    .collectMap(AgentDetailResponse::getUuid, Function.identity())
                    .flatMap(metadataMap -> {
                            metadataMap.values().stream()
                                    .filter(meta -> meta.getStatus() != AgentStatus.ACTIVE)
                                    .findFirst()
                                    .ifPresent(meta -> {
                                        throw new AgentInactiveException(meta.getUuid());
                                    });

                    return Flux.fromIterable(plan.getBlocks())
                            .flatMap(block -> blockExecutor.executeBlockReactive(
                                    block, metadataMap, new ConcurrentHashMap<>()
                            ))
                            .collectMap(TaskResult::getTaskId, Function.identity());
                })
                .timeout(Duration.ofSeconds(10));
        });
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
