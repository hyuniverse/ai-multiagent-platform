package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.model.ExecutionResult;
import java.util.Map;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;

public interface ResultPostProcessor {
    ExecutionResult process(Map<String,AgentResult> rawResults);
}
