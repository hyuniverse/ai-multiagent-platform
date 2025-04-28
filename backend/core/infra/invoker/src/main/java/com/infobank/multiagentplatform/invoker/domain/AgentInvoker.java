package com.infobank.multiagentplatform.invoker.domain;

import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;

public interface AgentInvoker {
    AgentResult invoke(AgentCallTask task);
}
