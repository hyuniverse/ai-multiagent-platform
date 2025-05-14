package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.exception.AgentEndpointConflictException;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAgentValidator implements AgentValidator {

    private final AgentRepository agentRepository;

    @Override
    public void validateForCreate(AgentMetadata metadata) {
        String endpoint = metadata.getEndpoint();
        if (agentRepository.existsByEndpoint(endpoint)) {
            throw new AgentEndpointConflictException(endpoint);
        }
    }

    @Override
    public void validateForUpdate(AgentMetadata metadata) {

    }
}
