package com.infobank.multiagentplatform.core.contract.agent.request;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PUT /agents/{id} 요청 바디용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentUpdateRequest {
    private String type;
    private ProtocolType protocol;
    private String endpoint;
    private boolean hasMemory;
    private String memoryType;
    private List<String> inputTypes;
    private List<String> outputTypes;
    private String description;
}
