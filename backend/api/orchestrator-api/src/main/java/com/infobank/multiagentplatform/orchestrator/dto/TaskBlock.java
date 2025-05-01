package com.infobank.multiagentplatform.orchestrator.dto;

import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 병렬로 실행 가능한 Task 묶음
 */
@Getter
@AllArgsConstructor
@ToString
public class TaskBlock {
    private final List<AgentTask> tasks;
}
