package com.infobank.multiagentplatform.core.contract.agent.request;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AgentInvocationRequest {

    private final String endpoint;
    private final String payload;
    private final boolean usesMemory;

    @Builder
    private AgentInvocationRequest(String endpoint, String payload, boolean usesMemory) {
        this.endpoint = endpoint;
        this.payload = payload;
        this.usesMemory = usesMemory;
    }

    public static AgentInvocationRequest of(AgentDetailResponse agentMetadata, String payload) {
        return AgentInvocationRequest.builder()
                .endpoint(agentMetadata.getEndpoint())
                .payload(payload)
                .usesMemory(agentMetadata.isHasMemory())
                .build();
    }

}
