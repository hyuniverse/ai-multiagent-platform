package com.infobank.multiagentplatform.broker.service.register;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;

public interface AgentRegistrationService {
    void register(AgentMetadata metadata);
}

