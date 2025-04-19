package com.infobank.multiagentplatform.invoker.domain;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;

public interface AgentInvoker {
    AgentResult invoke(AgentMetadata metadata, AgentRequest request);
}
