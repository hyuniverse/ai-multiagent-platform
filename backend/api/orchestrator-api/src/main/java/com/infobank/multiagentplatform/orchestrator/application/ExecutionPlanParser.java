package com.infobank.multiagentplatform.orchestrator.application;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ExecutionPlanParser {

    private final ObjectMapper objectMapper;

    public ExecutionPlanParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * LLM 응답 raw JSON을 ExecutionPlan 객체로 변환한다.
     */
    public ExecutionPlan parse(String rawJson) {
        try {
            // 중간 DTO 매핑
            PlanDto dto = objectMapper.readValue(rawJson, PlanDto.class);
            // ExecutionPlan 도메인으로 변환
            ExecutionPlan plan = new ExecutionPlan();
            // TaskBlock 매핑
            plan.setBlocks(
                    dto.getBlocks().stream()
                            .map(b -> new TaskBlock(
                                    b.getBlockId(),
                                    b.getTasks().stream()
                                            .map(t -> new AgentTask(t.getTaskId(), t.getAgentId(), t.getParameters(), t.isHasMemory()))
                                            .collect(Collectors.toList())
                            ))
                            .collect(Collectors.toList())
            );
            plan.setMetadata(dto.getMetadata());
            // 추가 검증
            validatePlan(plan);
            return plan;
        } catch (JsonProcessingException e) {
            throw new PlanParsingException("JSON parse error", e);
        }
    }

    private void validatePlan(ExecutionPlan plan) {
        // 예: 블록 순환 참조, 빈 블록 방지
        if (plan.getBlocks().isEmpty()) {
            throw new PlanParsingException("ExecutionPlan has no blocks");
        }
        // TODO: 추가 검증 로직
    }

    // 중간 매핑용 DTO
    private static class PlanDto {
        private List<BlockDto> blocks;
        private Map<String, Object> metadata;
        // getters/setters
    }
    private static class BlockDto {
        private String blockId;
        private List<TaskDto> tasks;
        // getters/setters
    }
    private static class TaskDto {
        private String taskId;
        private String agentId;
        private Map<String, Object> parameters;
        private boolean hasMemory;
        // getters/setters
    }
}
