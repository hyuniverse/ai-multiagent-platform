package com.infobank.multiagentplatform.orchestrator.application.executor;

import com.infobank.multiagentplatform.orchestrator.application.ResultPostProcessor;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionResult;
import com.infobank.multiagentplatform.orchestrator.model.TaskBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;

@Service
@RequiredArgsConstructor
public class ExecutionPlanExecutor {
    private final TaskBlockExecutor blockExecutor;
    private final ResultPostProcessor postProcessor;

    public ExecutionResult executePlan(ExecutionPlan plan) {
        Map<String,AgentResult> results = new ConcurrentHashMap<>();
        for (TaskBlock block : plan.getBlocks()) {
            blockExecutor.executeBlock(block, results);
        }
        return postProcessor.process(results);
    }
}
