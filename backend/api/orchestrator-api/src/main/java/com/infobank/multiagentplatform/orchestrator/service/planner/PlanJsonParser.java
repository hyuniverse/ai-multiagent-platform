package com.infobank.multiagentplatform.orchestrator.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import org.springframework.stereotype.Component;

import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.EMPTY_PLAN_EXCEPTION;
import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.JSON_PARSE_EXCEPTION;

@Component
public class PlanJsonParser {

    private final ObjectMapper objectMapper;

    public PlanJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * LLM 응답 raw JSON을 ExecutionPlan 도메인으로 변환한다.
     */
    public ExecutionPlan parse(String rawJson) {
        try {
            // Remove markdown code block if exists
            if (rawJson.startsWith("```")) {
                rawJson = rawJson.replaceAll("(?s)```(?:json)?\\s*", "")  // remove opening ```
                        .replaceAll("\\s*```$", "");            // remove closing ```
            }
            ExecutionPlan plan = objectMapper.readValue(rawJson, ExecutionPlan.class);
            validatePlan(plan);
            return plan;
        } catch (JsonProcessingException e) {
            throw new PlanParsingException(JSON_PARSE_EXCEPTION, e);
        }
    }

    private void validatePlan(ExecutionPlan plan) {
        if (plan.getBlocks().isEmpty()) {
            throw new PlanParsingException(EMPTY_PLAN_EXCEPTION);
        }
        // TODO: ATAM 기반 시나리오별 검증 로직 추가
    }
}
