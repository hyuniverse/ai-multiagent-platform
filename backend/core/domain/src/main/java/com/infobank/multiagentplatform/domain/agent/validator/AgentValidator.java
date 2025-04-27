package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;

public interface AgentValidator {
    void validate(AgentMetadata metadata);
}
