package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AgentSnapshotRepositoryCustom {
    Mono<AgentSnapshotEntity> saveWithEnumCast(AgentSnapshotEntity entity);
    Flux<AgentSnapshotEntity> saveAllWithEnumCast(List<AgentSnapshotEntity> entities);
}
