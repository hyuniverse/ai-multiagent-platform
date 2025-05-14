package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.model.plan.TaskBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
public class TaskBlockExecutor {
    private final SingleTaskExecutor singleTaskExecutor;
    private final TaskExecutor planTaskExecutor;

    @Autowired
    public TaskBlockExecutor(
            SingleTaskExecutor singleTaskExecutor,
            @Qualifier("applicationTaskExecutor") TaskExecutor planTaskExecutor
    ) {
        this.singleTaskExecutor = singleTaskExecutor;
        this.planTaskExecutor   = planTaskExecutor;
    }

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

