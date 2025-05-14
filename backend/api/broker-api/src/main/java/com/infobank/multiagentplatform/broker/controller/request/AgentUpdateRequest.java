package com.infobank.multiagentplatform.broker.controller.request;

import com.infobank.multiagentplatform.broker.service.request.AgentUpdateServiceRequest;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "에이전트 수정 요청 DTO")
public class AgentUpdateRequest {

    @NotBlank
    @Schema(description = "에이전트 이름", example = "summary-agent")
    private String name;

    @NotNull
    @Schema(description = "에이전트 타입", example = "REACT")
    private String type;

    @NotNull
    @Schema(description = "프로토콜 타입", example = "REST")
    private ProtocolType protocol;

    @NotBlank
    @Schema(description = "호출 엔드포인트", example = "http://localhost:8081/execute")
    private String endpoint;

    @NotNull
    @Schema(description = "메모리 사용 여부", example = "true")
    private boolean hasMemory;

    @Schema(description = "메모리 타입", example = "redis")
    private String memoryType;

    @NotNull
    @Schema(description = "입력 타입 목록", example = "[\"TEXT\"]")
    private List<String> inputTypes;

    @NotNull
    @Schema(description = "출력 타입 목록", example = "[\"TEXT\"]")
    private List<String> outputTypes;

    @Schema(description = "에이전트 설명")
    private String description;

    @Builder
    private AgentUpdateRequest(String name, String type, ProtocolType protocol, String endpoint,
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

    public AgentUpdateServiceRequest toServiceRequest() {
        return AgentUpdateServiceRequest.builder()
                .name(name)
                .type(type)
                .protocol(protocol)
                .endpoint(endpoint)
                .hasMemory(hasMemory)
                .memoryType(memoryType)
                .inputTypes(inputTypes)
                .outputTypes(outputTypes)
                .description(description)
                .build();
    }
}
