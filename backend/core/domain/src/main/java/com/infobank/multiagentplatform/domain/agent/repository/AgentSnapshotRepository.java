package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface AgentSnapshotRepository extends R2dbcRepository<AgentSnapshotEntity, String>, AgentSnapshotRepositoryCustom {
    Flux<AgentSnapshotEntity> findAllByUuidIn(Collection<String> uuids);

    @Query("SELECT a.* FROM agents a JOIN agent_snapshot s ON a.uuid = s.uuid WHERE s.reachable = true")
    Flux<AgentEntity> findAvailableAgents();
}
