package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.model.plan.TaskBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TaskBlockExecutor {
    private final SingleTaskExecutor singleTaskExecutor;
    private final TaskExecutor planTaskExecutor;

    public Flux<TaskResult> executeBlockReactive(TaskBlock block,
                                                 Map<String, AgentDetailResponse> metadataMap,
                                                 Map<String, TaskResult> results) {
        return Flux.fromIterable(block.getTasks())
                .flatMap(task ->
                        singleTaskExecutor
                                .executeReactive(task, metadataMap, results)
                                .subscribeOn(Schedulers.fromExecutor(planTaskExecutor))
                );
    }
}

