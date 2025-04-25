package com.infobank.multiagentplatform.invoker.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCallTask {

    private String agentId;
    private String endpoint;
    private String payload;
    private String protocol;
    private boolean hasMemory;

    // (필요 시 추가 필드)
    // private Map<String, String> headers;
}
