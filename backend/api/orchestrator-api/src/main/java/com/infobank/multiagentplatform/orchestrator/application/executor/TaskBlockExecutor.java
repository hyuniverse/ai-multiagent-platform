package com.infobank.multiagentplatform.orchestrator.application.executor;

import com.infobank.multiagentplatform.orchestrator.model.TaskBlock;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
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

    public void executeBlock(TaskBlock block, Map<String,AgentResult> results) {
        List<CompletableFuture<Void>> futures = block.getTasks().stream()
                .map(task -> CompletableFuture.runAsync(() -> {
                    AgentResult r = singleTaskExecutor.execute(task, results);
                    results.put(task.getAgentId(), r);
                }, planTaskExecutor))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
