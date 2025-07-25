package com.infobank.multiagentplatform.core.contract.agent.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@JsonDeserialize(builder = AgentDetailResponse.AgentDetailResponseBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentDetailResponse {

    private final String uuid;

    private final String name;
    private final String type;
    private final ProtocolType protocol;
    private final String endpoint;

    private final boolean hasMemory;
    private final String memoryType;

    private final List<String> inputTypes;
    private final List<String> outputTypes;
    private final String description;

    private final AgentStatus status;
    private final boolean reachable;
    private final int requestCount;

    @Builder
    private AgentDetailResponse(String uuid, String name, String type, ProtocolType protocol, String endpoint,
                                boolean hasMemory, String memoryType, List<String> inputTypes,
                                List<String> outputTypes, String description, AgentStatus status,
                                boolean reachable, int requestCount) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.protocol = protocol;
        this.endpoint = endpoint;
        this.hasMemory = hasMemory;
        this.memoryType = memoryType;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
        this.description = description;
        this.status = status;
        this.reachable = reachable;
        this.requestCount = requestCount;
    }

    public static AgentDetailResponse of(String uuid, AgentMetadata metadata, AgentSnapshot snapshot) {
        return AgentDetailResponse.builder()
                .uuid(uuid)
                .name(metadata.getName())
                .type(metadata.getType())
                .protocol(metadata.getProtocol())
                .endpoint(metadata.getEndpoint())
                .hasMemory(metadata.getMemory().isHasMemory())
                .memoryType(metadata.getMemory().getMemoryType())
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .status(snapshot.getStatus())
                .reachable(snapshot.isReachable())
                .requestCount(snapshot.getRequestCount())
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class AgentDetailResponseBuilder {}
}
