package com.infobank.multiagentplatform.orchestrator.util;

import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.invoker.domain.AgentCallTask;
import lombok.Getter;

@Getter
public class AgentCallTaskMapper {

    public static AgentCallTask from(AgentTask planTask, AgentMetadata metadata, String payload) {
        return AgentCallTask.builder()
                .agentId(planTask.getAgentId())
                .endpoint(metadata.getEndpoint())
                .payload(payload)
                .protocol(metadata.getProtocol())
                .hasMemory(metadata.getMemory().isHasMemory())
                .build();
    }
}
