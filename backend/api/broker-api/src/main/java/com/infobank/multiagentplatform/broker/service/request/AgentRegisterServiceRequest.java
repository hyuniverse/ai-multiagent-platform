package com.infobank.multiagentplatform.broker.service.request;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class AgentRegisterServiceRequest {

    private final String name;
    private final String type;
    private final ProtocolType protocol;
    private final String endpoint;
    private final boolean hasMemory;
    private final String memoryType;
    private final List<String> inputTypes;
    private final List<String> outputTypes;
    private final String description;

    @Builder
    private AgentRegisterServiceRequest(String name, String type, ProtocolType protocol, String endpoint,
                                        boolean hasMemory, String memoryType, List<String> inputTypes,
                                        List<String> outputTypes, String description) {
        this.name = name;
        this.type = type;
        this.protocol = protocol;
        this.endpoint = endpoint;
        this.hasMemory = hasMemory;
        this.memoryType = memoryType;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
        this.description = description;
    }

    public AgentMetadata toMetadata() {
        return AgentMetadata.builder()
                .name(name)
                .type(type)
                .protocol(protocol)
                .endpoint(endpoint)
                .memory(AgentMemory.of(hasMemory, memoryType))
                .inputTypes(inputTypes)
                .outputTypes(outputTypes)
                .description(description)
                .build();
    }
}
