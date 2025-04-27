package com.infobank.multiagentplatform.orchestrator.client;

import com.infobank.multiagentplatform.orchestrator.dto.AgentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgentSummaryCache {

    private final BrokerClient brokerClient;
    private volatile List<AgentSummary> cached = List.of();

    public List<AgentSummary> getCached() {
        return cached;
    }

    public void refresh() {
        this.cached = brokerClient.getAgentSummaries();
    }
}
