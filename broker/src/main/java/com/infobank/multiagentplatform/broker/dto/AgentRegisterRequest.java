package com.infobank.multiagentplatform.broker.dto;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentType;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.InputType;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "에이전트 등록 요청 DTO")
public class AgentRegisterRequest {

    @NotBlank
    @Schema(description = "에이전트 ID", example = "agent-001")
    private String id;

    @NotNull
    @Schema(description = "에이전트 타입", example = "REACT")
    private AgentType type;

    @NotNull
    @Schema(description = "프로토콜 타입", example = "REST")
    private ProtocolType protocol;

    @NotBlank
    @Schema(description = "호출 엔드포인트", example = "http://localhost:8081/execute")
    private String endpoint;

    @NotNull
    @Schema(description = "메모리 사용 여부", example = "true")
    private Boolean hasMemory;

    @Schema(description = "메모리 타입", example = "redis")
    private String memoryType;

    @NotNull
    @Schema(description = "입력 타입 목록", example = "[\"TEXT\"]")
    private List<InputType> inputTypes;

    @NotNull
    @Schema(description = "출력 타입 목록", example = "[\"TEXT\"]")
    private List<String> outputTypes;

    @Schema(description = "에이전트 설명")
    private String description;
}
