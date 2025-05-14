package com.infobank.multiagentplatform.broker.service.response;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AgentUpdateResponse {

    private final String uuid;
    private final String name;
    private final String type;
    private final String protocol;
    private final String endpoint;
    private final List<String> inputTypes;
    private final List<String> outputTypes;
    private final String description;
    private final LocalDateTime updatedAt;
    private final String message;

    @Builder
    private AgentUpdateResponse(String uuid, String name, String type, String protocol, String endpoint,
                                List<String> inputTypes, List<String> outputTypes, String description,
                                LocalDateTime updatedAt, String message) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.protocol = protocol;
        this.endpoint = endpoint;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
        this.description = description;
        this.updatedAt = updatedAt;
        this.message = message;
    }

    public static AgentUpdateResponse of(AgentMetadata metadata, String uuid) {
        return AgentUpdateResponse.builder()
                .uuid(uuid)
                .name(metadata.getName())
                .type(metadata.getType())
                .protocol(metadata.getProtocol().name())
                .endpoint(metadata.getEndpoint())
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .updatedAt(LocalDateTime.now())
                .message("에이전트가 정상적으로 수정되었습니다.")
                .build();
    }
}
