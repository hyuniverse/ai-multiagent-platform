package com.infobank.multiagentplatform.invoker.domain;

import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.resilience.logging.ExecutionContext;

public interface AgentInvoker {
    AgentResult invoke(AgentCallTask task, ExecutionContext context);
}
