package com.infobank.multiagentplatform.orchestrator.service.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import org.springframework.stereotype.Component;

import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.*;

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
            System.out.println("Generated Plan JSON: " + rawJson);
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
        if (plan.getBlocks() == null || plan.getBlocks().isEmpty()) {
            throw new PlanParsingException(EMPTY_PLAN_EXCEPTION);
        }
        plan.getBlocks().forEach(block -> {
            if (block.getTasks() == null) {
                throw new PlanParsingException(INVALID_BLOCK_SCHEMA);
            }
            if (block.getTasks().isEmpty()) {
                throw new PlanParsingException(EMPTY_TASKS_EXCEPTION);
            }
        });
    }
}
