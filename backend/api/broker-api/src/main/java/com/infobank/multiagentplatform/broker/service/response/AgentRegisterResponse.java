package com.infobank.multiagentplatform.broker.service.response;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AgentRegisterResponse {

    private final String uuid;
    private final String name;
    private final String type;
    private final String protocol;
    private final String endpoint;
    private final List<String> inputTypes;
    private final List<String> outputTypes;
    private final String description;
    private final LocalDateTime registeredAt;
    private final String message;

    @Builder
    private AgentRegisterResponse(String uuid, String name, String type, String protocol, String endpoint, List<String> inputTypes, List<String> outputTypes, String description, LocalDateTime registeredAt, String message) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.protocol = protocol;
        this.endpoint = endpoint;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
        this.description = description;
        this.registeredAt = registeredAt;
        this.message = message;
    }

    public static AgentRegisterResponse of(AgentMetadata metadata, String uuid) {
        return AgentRegisterResponse.builder()
                .uuid(uuid)
                .name(metadata.getName())
                .type(metadata.getType())
                .protocol(metadata.getProtocol().name())
                .endpoint(metadata.getEndpoint())
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .description(metadata.getDescription())
                .registeredAt(LocalDateTime.now())
                .message("에이전트가 정상적으로 등록되었습니다.")
                .build();
    }
}
