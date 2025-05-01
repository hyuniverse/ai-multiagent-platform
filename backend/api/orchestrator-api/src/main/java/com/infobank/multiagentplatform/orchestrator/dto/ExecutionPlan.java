package com.infobank.multiagentplatform.orchestrator.dto;

import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 실행 계획 전체 흐름
 * - blocks: 순차 실행 블록 리스트
 * - unassigned: 매칭 실패하거나 validation 미통과된 task들
 */
@Getter
@AllArgsConstructor
@ToString
public class ExecutionPlan {
    private final List<TaskBlock> blocks;
    private final List<AgentTask> unassigned;
}
