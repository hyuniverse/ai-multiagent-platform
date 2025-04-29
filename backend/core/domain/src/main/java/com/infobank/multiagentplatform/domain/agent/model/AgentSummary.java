package com.infobank.multiagentplatform.domain.agent.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Agent를 LLM에게 전달할 수 있도록 요약한 구조
 */
@Getter
@Setter
public class AgentSummary {
    private String agentId;
    private String description;
    private String inputType;
    private String outputType;
}
