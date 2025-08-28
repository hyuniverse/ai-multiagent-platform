package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import reactor.core.publisher.Mono;

public interface AgentValidator {
    Mono<Void> validateForCreate(AgentMetadata metadata);
    Mono<Void> validateForUpdate(AgentMetadata metadata);
}