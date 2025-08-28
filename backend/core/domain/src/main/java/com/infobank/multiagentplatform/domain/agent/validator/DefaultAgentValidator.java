package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.exception.AgentEndpointConflictException;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DefaultAgentValidator implements AgentValidator {

    private final AgentRepository agentRepository;

    @Override
    public Mono<Void> validateForCreate(AgentMetadata metadata) {
        String endpoint = metadata.getEndpoint();

        return agentRepository.existsByEndpoint(endpoint)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new AgentEndpointConflictException(endpoint));
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> validateForUpdate(AgentMetadata metadata) {
        return Mono.empty();
    }
}