package com.infobank.multiagentplatform.invoker.application;

import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.infrastructure.rest.RestAgentInvoker;
import com.infobank.multiagentplatform.invoker.exception.UnsupportedProtocolException;

public class AgentInvokerFactory {
    public AgentInvoker getInvoker(String protocol) {
        return switch (protocol.toLowerCase()) {
            case "rest" -> new RestAgentInvoker();
            default -> throw new UnsupportedProtocolException(protocol);
        };
    }
}