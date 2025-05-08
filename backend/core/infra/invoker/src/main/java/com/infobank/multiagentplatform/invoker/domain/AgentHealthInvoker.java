package com.infobank.multiagentplatform.invoker.domain;

public interface AgentHealthInvoker {
    boolean ping(String endpoint);
}