package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.plan.TaskBlock;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionPlanExecutor {

    private final BrokerClient brokerClient;        // 이제 Mono<List<AgentDetailResponse>> 반환
    private final TaskBlockExecutor blockExecutor; // Flux<TaskResult> 반환

    @CircuitBreaker(name = "executor-circuit", fallbackMethod = "fallbackExecutePlanReactive")
    @Retry(name = "executor-retry")
    public Mono<Map<String, TaskResult>> executePlanReactive(ExecutionPlan plan) {
        // 1) 모든 에이전트 ID 수집
        List<String> agentIds = plan.getBlocks().stream()
                .flatMap(b -> b.getTasks().stream().map(AgentTask::getAgentId))
                .distinct()
                .collect(Collectors.toList());

        // 2) metadataMap 준비 & 블록별 Reactive 실행 → Map<String, TaskResult> 생성
        return Mono
                .fromCallable(() -> brokerClient.getAgentMetadataBatch(agentIds))
                .flatMapMany(Flux::fromIterable)
                .collectMap(AgentDetailResponse::getUuid, Function.identity())
                .flatMap(metadataMap ->
                        Flux.fromIterable(plan.getBlocks())
                                .flatMap(block ->
                                        blockExecutor.executeBlockReactive(
                                                block,
                                                metadataMap,
                                                new ConcurrentHashMap<>()
                                        )
                                )
                                .collectMap(TaskResult::getTaskId, tr -> tr)

                )
                .timeout(Duration.ofMillis(10000));
    }

    private Mono<Map<String, TaskResult>> fallbackExecutePlanReactive(
            ExecutionPlan plan,
            Throwable ex
    ) {
        Map<String, TaskResult> fallback = new HashMap<>();
        for (TaskBlock block : plan.getBlocks()) {
            for (AgentTask task : block.getTasks()) {
                fallback.put(
                        task.getId(),
                        TaskResult.of(
                                task.getId(),
                                null,
                                Map.of(
                                        "fallback", "Execution failed",
                                        "reason", ex.getClass().getSimpleName() + ": " + ex.getMessage()
                                )
                        )
                );
            }
        }
        return Mono.just(fallback);
    }
}
