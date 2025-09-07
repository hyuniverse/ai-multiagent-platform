package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RestAgentHealthInvoker implements AgentHealthInvoker {

    private static final Logger log = LoggerFactory.getLogger(RestAgentHealthInvoker.class);
    private final WebClient webClient;
    private final Duration healthTimeout;

    public RestAgentHealthInvoker(WebClient.Builder webClientBuilder,
                                  @Value("${orchestrator.timeouts.agent-health:500ms}") Duration healthTimeout) {
        this.webClient = webClientBuilder.build();
        this.healthTimeout = healthTimeout;
    }

    @Override
    public Mono<Boolean> ping(String endpoint) {
        return webClient.get()
                .uri(endpoint + "/ping")
                .retrieve()
                .toBodilessEntity()
                .timeout(healthTimeout)
                .map(response -> true)
                .doOnError(e -> log.warn("Health check failed for {}: {}", endpoint, e.toString()))
                .onErrorReturn(false);
    }
}
