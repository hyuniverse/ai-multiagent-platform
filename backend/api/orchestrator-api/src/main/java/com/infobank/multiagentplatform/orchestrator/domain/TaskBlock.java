package com.infobank.multiagentplatform.orchestrator.domain;

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
