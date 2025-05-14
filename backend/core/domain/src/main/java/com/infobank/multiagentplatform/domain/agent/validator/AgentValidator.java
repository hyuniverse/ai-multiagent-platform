package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;

public interface AgentValidator {
    void validateForCreate(AgentMetadata metadata);
    void validateForUpdate(AgentMetadata metadata);
}
