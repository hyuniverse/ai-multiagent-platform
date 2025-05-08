package com.infobank.multiagentplatform.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 병렬로 실행 가능한 Task 묶음
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskBlock {
    @JsonProperty("tasks")
    private List<AgentTask> tasks;
}
