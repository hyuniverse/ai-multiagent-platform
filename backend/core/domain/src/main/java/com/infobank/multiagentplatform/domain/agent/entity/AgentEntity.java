package com.infobank.multiagentplatform.domain.agent.entity;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("agents")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentEntity implements Persistable<String> {

    @Id
    private String uuid;

    private String name;

    private String endpoint;

    private String type;

    @Column("protocol")
    private String protocol;

    private boolean hasMemory;
    
    @Column("memory_type")
    private String memoryType;

    private String description;

    private List<String> inputTypes;

    private List<String> outputTypes;

    @Transient
    private boolean newEntity;

    public static AgentEntity create(AgentMetadata metadata) {
        String memoryType = metadata.getMemory().getMemoryType();
        if (memoryType == null || memoryType.trim().isEmpty()) {
            memoryType = "";
        }
        
        return AgentEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name(metadata.getName())
                .type(metadata.getType())
                .protocol(metadata.getProtocol().name())
                .endpoint(metadata.getEndpoint())
                .hasMemory(metadata.getMemory().isHasMemory())
                .memoryType(memoryType)
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .newEntity(true)
                .build();
    }

    public AgentEntity updateWith(AgentMetadata metadata) {
        String memoryType = metadata.getMemory().getMemoryType();
        if (memoryType == null || memoryType.trim().isEmpty()) {
            memoryType = "";
        }
        
        return this.toBuilder()
                .name(metadata.getName())
                .endpoint(metadata.getEndpoint())
                .type(metadata.getType())
                .protocol(metadata.getProtocol().name())
                .hasMemory(metadata.getMemory().isHasMemory())
                .memoryType(memoryType)
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .build();
    }

    @Override
    public String getId() {
        return this.uuid;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.newEntity;
    }
}