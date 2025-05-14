package com.infobank.multiagentplatform.domain.agent.processor;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;

public interface AgentPostProcessor {
    void afterRegister(AgentMetadata metadata);

    void afterUpdate(AgentMetadata metadata);

    void afterDelete(String uuid);
}
