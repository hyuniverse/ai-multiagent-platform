package com.infobank.multiagentplatform.orchestrator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 사용자 요청 기반 실행 계획 수립을 위한 입력 DTO
 */
@Getter
@Setter
@Schema(description = "사용자 요청 기반 실행 계획 수립 요청")
public class StandardRequest {

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
}
