package com.infobank.multiagentplatform.core.contract.agent.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에이전트 호출 결과를 담는 DTO
 */
@Getter
public class AgentInvocationResponse {
    private final String rawResponse;
    private final JsonNode parsedResult;

    @Builder
    private AgentInvocationResponse(String rawResponse, JsonNode parsedResult) {
        this.rawResponse = rawResponse;
        this.parsedResult = parsedResult;
    }

    public static AgentInvocationResponse of(String raw, JsonNode parsed) {
        return new AgentInvocationResponse(raw, parsed);
    }
}