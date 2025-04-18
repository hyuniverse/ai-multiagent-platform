package com.infobank.multiagentplatform.domain.agent.processor;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import org.springframework.stereotype.Component;

@Component
public class LoggingAgentPostProcessor implements AgentPostProcessor {

    @Override
    public void afterRegister(AgentMetadata metadata) {
        System.out.println("✅ Agent 등록 완료: " + metadata.getId());
    }
}
