package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers; // Schedulers import

@Component
@RequiredArgsConstructor
public class AgentHealthChecker {

    private final AgentInvokerFactory factory;

    public Mono<Boolean> isReachable(ProtocolType protocol, String endpoint) {
        AgentHealthInvoker invoker = factory.getHealthInvoker(protocol);

        return Mono.fromCallable(() -> invoker.ping(endpoint))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorReturn(false);
    }
}