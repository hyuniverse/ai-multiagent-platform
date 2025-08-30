package com.infobank.multiagentplatform.invoker.domain;

import reactor.core.publisher.Mono;

public interface AgentHealthInvoker {
    Mono<Boolean> ping(String endpoint);
}