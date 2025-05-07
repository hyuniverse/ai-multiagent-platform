package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AgentSnapshotRepository extends JpaRepository<AgentSnapshotEntity, String> {
    List<AgentSnapshotEntity> findAllByUuidIn(Collection<String> uuids);
}
