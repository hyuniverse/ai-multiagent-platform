package com.infobank.multiagentplatform.domain.agent.processor;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import org.springframework.stereotype.Component;

@Component
public class LoggingAgentPostProcessor implements AgentPostProcessor {

    @Override
    public void afterRegister(AgentMetadata metadata) {
        System.out.println("✅ Agent 등록 완료: " + metadata.getEndpoint());
    }

    @Override
    public void afterUpdate(AgentMetadata metadata) {
        System.out.println("✅ Agent 수정 완료: " + metadata.getEndpoint());
    }

    @Override
    public void afterDelete(String uuid) {
        System.out.println("✅ Agent 삭제 완료: " + uuid);
    }
}
