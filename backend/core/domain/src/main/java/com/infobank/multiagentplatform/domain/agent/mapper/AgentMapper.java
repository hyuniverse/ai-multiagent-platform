package com.infobank.multiagentplatform.domain.agent.mapper;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    AgentSnapshot toSnapshot(AgentSnapshotEntity entity);

    AgentMetadata toMetadata(AgentEntity entity);

    AgentEntity toEntity(AgentMetadata metadata);

    AgentSnapshotEntity toSnapshotEntity(AgentSnapshot snapshot);

    AgentSummary toSummary(AgentMetadata metadata);
}