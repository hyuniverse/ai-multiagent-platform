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
    @GeneratedValue(strategy = GenerationType.UUID)
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

    public static AgentEntity from(AgentMetadata metadata, String uuid) {
        return AgentEntity.builder()
                .uuid(uuid)
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
}
