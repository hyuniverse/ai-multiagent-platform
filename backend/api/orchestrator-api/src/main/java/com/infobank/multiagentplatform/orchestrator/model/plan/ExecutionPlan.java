package com.infobank.multiagentplatform.orchestrator.model.plan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 실행 계획 전체 흐름
 * - blocks: 순차 실행 블록 리스트
 * - unassigned: 매칭 실패하거나 validation 미통과된 task들
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExecutionPlan {
    @JsonProperty("blocks")
    private List<TaskBlock> blocks;

    @JsonProperty("unassigned")
    private List<AgentTask> unassigned;
}
