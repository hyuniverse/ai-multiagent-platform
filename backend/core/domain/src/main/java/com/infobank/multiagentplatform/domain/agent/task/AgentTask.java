package com.infobank.multiagentplatform.domain.agent.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 하나의 에이전트 task 정보
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@ToString
public class AgentTask {
    private final String goal;
    private final String agentId;         // null이면 아직 매칭되지 않은 task
    private final String inputType;
    private final String inputFrom;       // optional: 선행 task의 taskId

    @JsonCreator
    public AgentTask(
            @JsonProperty("goal") String goal,
            @JsonProperty("agentId") String agentId,
            @JsonProperty("inputType") String inputType,
            @JsonProperty("inputFrom") String inputFrom
    ) {
        this.goal = goal;
        this.agentId = agentId;
        this.inputType = inputType;
        this.inputFrom = inputFrom;
    }
}