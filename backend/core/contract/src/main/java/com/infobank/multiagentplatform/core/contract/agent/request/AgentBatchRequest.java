package com.infobank.multiagentplatform.core.contract.agent.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /agents/batch 요청 바디용
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AgentBatchRequest {
    @NotEmpty
    private List<String> ids;
}
