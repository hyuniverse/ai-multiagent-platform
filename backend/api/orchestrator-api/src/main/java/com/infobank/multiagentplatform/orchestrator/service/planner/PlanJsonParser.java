package com.infobank.multiagentplatform.orchestrator.service.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.*;

@Component
public class PlanJsonParser {

    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry; // 추가
    private final Counter planParseSuccessCounter; // 추가
    private final Counter planParseFailureCounter; // 추가

    public PlanJsonParser(ObjectMapper objectMapper, MeterRegistry meterRegistry) { // 수정
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry; // 추가
        this.planParseSuccessCounter = Counter.builder("orchestration.plan.parse")
                .tag("status", "success")
                .description("Counts successful plan parsing")
                .register(meterRegistry);
        this.planParseFailureCounter = Counter.builder("orchestration.plan.parse")
                .tag("status", "failure")
                .description("Counts failed plan parsing")
                .register(meterRegistry);
    }

    /**
     * LLM 응답 raw JSON을 ExecutionPlan 도메인으로 변환한다.
     */
    public ExecutionPlan parse(String rawJson) {
        try {
            System.out.println("Generated Plan JSON: " + rawJson);
            if (rawJson.startsWith("```")) {
                rawJson = rawJson.replaceAll("(?s)```(?:json)?\\s*", "")
                        .replaceAll("\\s*```$", "");
            }
            ExecutionPlan plan = objectMapper.readValue(rawJson, ExecutionPlan.class);
            validatePlan(plan);
            planParseSuccessCounter.increment(); // 성공 카운트 증가
            return plan;
        } catch (JsonProcessingException | PlanParsingException e) { // PlanParsingException 포함
            planParseFailureCounter.increment(); // 실패 카운트 증가
            if (e instanceof PlanParsingException) {
                throw (PlanParsingException) e;
            }
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
