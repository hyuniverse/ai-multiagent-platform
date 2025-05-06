package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<AgentEntity, String> {
    boolean existsByEndpoint(String endpoint);
}