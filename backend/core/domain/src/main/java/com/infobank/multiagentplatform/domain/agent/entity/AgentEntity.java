package com.infobank.multiagentplatform.domain.agent.entity;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "agents")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ✅ 여기에 붙입니다.
    @Column(length = 36)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String endpoint;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolType protocol;

    @Embedded
    private AgentMemory memory;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_input_types", joinColumns = @JoinColumn(name = "agent_uuid"))
    private List<String> inputTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_output_types", joinColumns = @JoinColumn(name = "agent_uuid"))
    private List<String> outputTypes;

    @Column(length = 512)
    private String description;

    public static AgentEntity from(AgentMetadata metadata) {
        return AgentEntity.builder()
                .name(metadata.getName())
                .type(metadata.getType())
                .protocol(metadata.getProtocol())
                .endpoint(metadata.getEndpoint())
                .memory(metadata.getMemory())
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .build();
    }

    public void updateFrom(AgentMetadata metadata) {
        this.name = metadata.getName();
        this.endpoint = metadata.getEndpoint();
        this.type = metadata.getType();
        this.protocol = metadata.getProtocol();
        this.memory = metadata.getMemory();
        this.inputTypes = metadata.getInputTypes();
        this.outputTypes = metadata.getOutputTypes();
        this.description = metadata.getDescription();
    }
}
