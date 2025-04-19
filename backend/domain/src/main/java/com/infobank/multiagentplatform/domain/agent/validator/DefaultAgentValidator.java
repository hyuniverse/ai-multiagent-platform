package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import org.springframework.stereotype.Component;

@Component
public class DefaultAgentValidator implements AgentValidator {

    @Override
    public void validate(AgentMetadata metadata) {
        if (metadata.getId() == null || metadata.getId().isBlank()) {
            throw new IllegalArgumentException("Agent ID must not be blank");
        }

        if (metadata.getEndpoint() == null || metadata.getEndpoint().isBlank()) {
            throw new IllegalArgumentException("Endpoint must not be blank");
        }

        // 기타 추가 조건 가능
    }
}
