package com.infobank.multiagentplatform.domain.agent.task;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
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
    private ProtocolType protocol;
    private boolean hasMemory;

    // (필요 시 추가 필드)
    // private Map<String, String> headers;
}
