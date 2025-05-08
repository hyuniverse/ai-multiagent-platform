package com.infobank.multiagentplatform.orchestrator.controller.request;

import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 사용자 요청 기반 실행 계획 수립을 위한 입력 DTO
 */
@Getter
@Schema(description = "사용자 요청 기반 실행 계획 수립 요청")
@NoArgsConstructor
public class OrchestrationRequest {

    @NotBlank
    @Schema(description = "사용자의 자연어 입력", example = "Summarize this article and detect sentiment.")
    private String rawText;

    @NotBlank
    @Schema(description = "입력 유형 (예: text, image, file, audio)", example = "text")
    private String inputType;

    @Schema(description = "첨부 파일명 (선택)", example = "report.pdf")
    private String fileName;

    @Schema(description = "도메인별 추가 메타데이터", example = "{\"resolution\": \"1080p\", \"sensor\": \"lidar-v1\"}")
    private Map<String, String> metadata;

    @Builder
    private OrchestrationRequest(String rawText, String inputType, String fileName, Map<String, String> metadata) {
        this.rawText = rawText;
        this.inputType = inputType;
        this.fileName = fileName;
        this.metadata = metadata;
    }

    public OrchestrationServiceRequest toServiceRequest() {
        return OrchestrationServiceRequest.builder()
                .rawText(rawText)
                .inputType(inputType)
                .fileName(fileName)
                .metadata(metadata)
                .build();
    }

}
