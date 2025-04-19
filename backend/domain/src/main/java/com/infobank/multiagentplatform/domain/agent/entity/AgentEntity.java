package com.infobank.multiagentplatform.domain.agent.entity;

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
    private String id;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    private ProtocolType protocol;

    @Column(nullable = false)
    private String endpoint;

    @Embedded
    private AgentMemory memory;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_input_types", joinColumns = @JoinColumn(name = "agent_id"))
    private List<String> inputTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_output_types", joinColumns = @JoinColumn(name = "agent_id"))
    private List<String> outputTypes;

    @Column(length = 512)
    private String description;
}
