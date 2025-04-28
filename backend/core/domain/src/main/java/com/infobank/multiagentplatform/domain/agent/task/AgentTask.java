package com.infobank.multiagentplatform.domain.agent.task;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AgentTask {
    private final String goal;
    private final String agentId;         // null이면 아직 매칭되지 않은 task
    private final String inputType;
    private final String inputFrom;       // optional: 선행 task의 taskId
}
