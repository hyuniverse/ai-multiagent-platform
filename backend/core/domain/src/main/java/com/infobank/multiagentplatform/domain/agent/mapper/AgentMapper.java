package com.infobank.multiagentplatform.domain.agent.mapper;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "spring")
public interface AgentMapper {
    
    Logger log = LoggerFactory.getLogger(AgentMapper.class);

    @Mapping(target = "status", expression = "java(entity.getStatusAsEnum())")
    AgentSnapshot toSnapshot(AgentSnapshotEntity entity);

    @Mapping(target = "memory", expression = "java(com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory.of(entity.isHasMemory(), entity.getMemoryType()))")
    @Mapping(target = "protocol", expression = "java(com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType.valueOf(entity.getProtocol().toUpperCase()))")
    AgentMetadata toMetadata(AgentEntity entity);

    @Mapping(target = "hasMemory", expression = "java(metadata.getMemory().isHasMemory())")
    @Mapping(target = "memoryType", expression = "java(metadata.getMemory().getMemoryType())")
    @Mapping(target = "newEntity", constant = "false") // 기존 엔티티로 설정
    @Mapping(target = "uuid", ignore = true) // UUID는 매핑하지 않음
    AgentEntity toEntity(AgentMetadata metadata);

    @Mapping(target = "status", expression = "java(snapshot.getStatus() != null ? snapshot.getStatus() : null)")
    AgentSnapshotEntity toSnapshotEntity(AgentSnapshot snapshot);

    @Mapping(target = "agentId", ignore = true) // UUID는 별도로 처리
    @Mapping(target = "inputType", expression = "java(metadata.getInputTypes() != null && !metadata.getInputTypes().isEmpty() ? String.join(\",\", metadata.getInputTypes()) : \"\")")
    @Mapping(target = "outputType", expression = "java(metadata.getOutputTypes() != null && !metadata.getOutputTypes().isEmpty() ? String.join(\",\", metadata.getOutputTypes()) : \"\")")
    AgentSummary toSummary(AgentMetadata metadata);
    
    // for debug
    default AgentSnapshot toSnapshotWithLogging(AgentSnapshotEntity entity) {
        if (entity == null) {
//            log.warn("AgentSnapshotEntity is null in mapper");
            return null;
        }
        
//        log.info("Mapper - Input Entity: uuid={}, status(String)={}, statusAsEnum={}, reachable={}",
//            entity.getUuid(), entity.getStatus(), entity.getStatusAsEnum(), entity.isReachable());
            
        AgentSnapshot result = toSnapshot(entity);
        
//        log.info("Mapper - Output Snapshot: status={}, reachable={}",
//            result != null ? result.getStatus() : "null",
//            result != null ? result.isReachable() : "null");
//
        return result;
    }
}