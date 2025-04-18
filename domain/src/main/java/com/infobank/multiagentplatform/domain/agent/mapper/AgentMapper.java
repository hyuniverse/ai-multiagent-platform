package com.infobank.multiagentplatform.domain.agent.mapper;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AgentMapper {

    AgentMapper INSTANCE = Mappers.getMapper(AgentMapper.class);

    AgentEntity toEntity(AgentMetadata metadata);

    AgentMetadata toModel(AgentEntity entity);

    AgentSnapshotEntity toEntity(AgentSnapshot snapshot);

    AgentSnapshot toModel(AgentSnapshotEntity entity);
}