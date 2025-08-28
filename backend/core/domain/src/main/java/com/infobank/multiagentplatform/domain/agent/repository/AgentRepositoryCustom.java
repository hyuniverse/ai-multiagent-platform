package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import reactor.core.publisher.Mono;

public interface AgentRepositoryCustom {
    Mono<AgentEntity> saveWithEnumCast(AgentEntity entity);
}
