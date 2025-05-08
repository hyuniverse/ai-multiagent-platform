package com.infobank.multiagentplatform.invoker.application;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import com.infobank.multiagentplatform.invoker.infrastructure.rest.RestAgentInvoker;
import com.infobank.multiagentplatform.invoker.infrastructure.rest.RestAgentHealthInvoker;
import com.infobank.multiagentplatform.invoker.exception.UnsupportedProtocolException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentInvokerFactory {
    private final RestAgentInvoker restAgentInvoker;
    private final RestAgentHealthInvoker restAgentHealthInvoker;

    public AgentInvoker getInvoker(ProtocolType protocol) {
        if (protocol == ProtocolType.REST) {
            return restAgentInvoker;
        }
        throw new UnsupportedProtocolException(protocol.name());
    }

    public AgentHealthInvoker getHealthInvoker(ProtocolType protocol) {
        if (protocol == ProtocolType.REST) {
            return restAgentHealthInvoker;
        }
        throw new UnsupportedProtocolException(protocol.name());
    }
}