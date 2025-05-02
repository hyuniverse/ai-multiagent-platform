package com.infobank.multiagentplatform.orchestrator.model;

import java.util.List;
import java.util.Map;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
import lombok.Getter;

@Getter
public class ExecutionResult {
    private final List<TaskResult> orderedResults;
    private final Map<String,AgentResult> rawResults;

    public ExecutionResult(List<TaskResult> orderedResults, Map<String,AgentResult> rawResults) {
        this.orderedResults = orderedResults;
        this.rawResults = rawResults;
    }

    public List<TaskResult> getOrderedResults() {
        return orderedResults;
    }

    public Map<String,AgentResult> getRawResults() {
        return rawResults;
    }
}