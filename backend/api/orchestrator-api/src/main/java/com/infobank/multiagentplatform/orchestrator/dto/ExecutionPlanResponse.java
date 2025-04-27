package com.infobank.multiagentplatform.orchestrator.dto;

import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * ExecutionPlan 결과를 프론트에 전달하기 위한 구조
 */
@Getter
@Builder
public class ExecutionPlanResponse {
    private List<AgentTask> tasks;        // 계획에 포함된 task
    private List<AgentTask> unassigned;   // 할당되지 못한 task
    private List<String> logs;
}
