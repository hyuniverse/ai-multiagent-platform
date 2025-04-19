package com.infobank.multiagentplatform.domain.agent.model;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentMetadata {

    private String id;
    private String type;
    private ProtocolType protocol;
    private String endpoint;
    private AgentMemory memory;
    private List<String> inputTypes;
    private List<String> outputTypes;
    private String description;

    public AgentMetadata() {}
}