// AgentBatchRequest.java
package com.infobank.multiagentplatform.broker.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /agents/batch 요청 바디용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentBatchRequest {
    private List<String> ids;
}
