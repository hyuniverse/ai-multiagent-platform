package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentSnapshotRepository extends JpaRepository<AgentSnapshotEntity, String> {
}
