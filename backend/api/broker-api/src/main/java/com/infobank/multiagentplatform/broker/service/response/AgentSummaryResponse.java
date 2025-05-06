// AgentSummaryResponse.java
package com.infobank.multiagentplatform.broker.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GET /agents/summaries 응답용 (경량 정보)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentSummaryResponse {
    private String agentId;
    private String description;
    private String inputType;
    private String outputType;
}
