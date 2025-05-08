package com.infobank.multiagentplatform.orchestrator.service.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OrchestrationServiceRequest {
    private final String rawText;
    private final String inputType;
    private final String fileName;
    private final Map<String, String> metadata;

    @Builder
    private OrchestrationServiceRequest(String rawText, String inputType, String fileName, Map<String, String> metadata) {
        this.rawText = rawText;
        this.inputType = inputType;
        this.fileName = fileName;
        this.metadata = metadata;
    }
}
