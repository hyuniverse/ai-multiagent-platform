package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.model.plan.TaskBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;

@Component
public class TaskBlockExecutor {
    private final SingleTaskExecutor singleTaskExecutor;
    private final ReactiveMetricOperator metricOperator;

    @Autowired
    public TaskBlockExecutor(
            SingleTaskExecutor singleTaskExecutor,
            ReactiveMetricOperator metricOperator
    ) {
        this.singleTaskExecutor = singleTaskExecutor;
        this.metricOperator = metricOperator;
    }

    public Flux<TaskResult> executeBlockReactive(TaskBlock block,
                                                 Map<String, AgentDetailResponse> metadataMap,
                                                 Map<String, TaskResult> results) {
        return Flux.fromIterable(block.getTasks())
                .flatMap(task ->
                        singleTaskExecutor
                                .executeReactive(task, metadataMap, results)
                );
    }
}

