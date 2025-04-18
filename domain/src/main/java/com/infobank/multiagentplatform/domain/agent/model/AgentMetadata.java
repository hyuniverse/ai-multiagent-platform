package com.infobank.multiagentplatform.domain.agent.model;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentType;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.InputType;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentMetadata {

    private String id;
    private AgentType type;
    private ProtocolType protocol;
    private String endpoint;
    private AgentMemory memory;
    private List<InputType> inputTypes;
    private List<String> outputTypes;
    private String description;
}