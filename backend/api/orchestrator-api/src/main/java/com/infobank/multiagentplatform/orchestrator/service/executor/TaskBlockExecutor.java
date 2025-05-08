package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.model.plan.TaskBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TaskBlockExecutor {
    private final SingleTaskExecutor singleTaskExecutor;
    private final TaskExecutor planTaskExecutor;

    public void executeBlock(
            TaskBlock block,
            Map<String, AgentDetailResponse> metadataMap,
            Map<String, TaskResult> results
    ) {
        List<CompletableFuture<Void>> futures = block.getTasks().stream()
                .map(task -> CompletableFuture.runAsync(() -> {
                    TaskResult r = singleTaskExecutor.execute(task, metadataMap, results);
                    results.put(task.getId(), r); // taskId, taskResult
                }, planTaskExecutor))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
