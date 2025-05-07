package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AgentRepository extends JpaRepository<AgentEntity, String> {
    boolean existsByEndpoint(String endpoint);

    List<AgentEntity> findAllByUuidIn(Collection<String> uuids);
}