package com.infobank.multiagentplatform.orchestrator.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrchestrationResponse {
    private final String narrative;

    public static OrchestrationResponse of(String narrative) {
        return new OrchestrationResponse(narrative);
    }
}