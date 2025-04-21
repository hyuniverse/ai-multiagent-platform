package com.infobank.multiagentplatform.orchestrator.client;

import com.infobank.multiagentplatform.orchestrator.dto.AgentSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrokerClient {
    public List<AgentSummary> getAgentSummaries() {
        // TODO: 실제로는 Broker 서버로부터 가져와야 함
        AgentSummary mock = new AgentSummary();
        mock.setAgentId("summarizer-v1");
        mock.setDescription("Summarizes text into markdown");
        mock.setInputType("text");
        mock.setOutputType("markdown");
        return List.of(mock);
    }
}
