package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentHealthChecker {

    private final AgentInvokerFactory factory;

    public boolean isReachable(ProtocolType protocol, String endpoint) {
        AgentHealthInvoker invoker = factory.getHealthInvoker(protocol);
        return invoker.ping(endpoint);
    }
}