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
@ToString
public class AgentTask {
    private final String id;             // 이 태스크의 고유 ID (plan DSL 내부용)
    private final String agentId;        // 호출할 에이전트 UUID
    private final String instruction;    // what to do (goal 또는 payload 템플릿)
    private final String inputFrom;      // 이전 태스크 ID 참조 (없으면 null)

    @Builder
    @JsonCreator
    private AgentTask(
            @JsonProperty("taskId") String id,
            @JsonProperty("agent-UUId") String agentId,
            @JsonProperty("instruction") String instruction,
            @JsonProperty("inputFrom") String inputFrom
    ) {
        this.id = id;
        this.agentId = agentId;
        this.instruction = instruction;
        this.inputFrom = inputFrom;
    }

    public static AgentTask of(String id, String agentId, String instruction, String inputFrom) {
        return AgentTask.builder()
                .id(id)
                .agentId(agentId)
                .instruction(instruction)
                .inputFrom(inputFrom)
                .build();
    }
}