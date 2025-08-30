package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RestAgentHealthInvoker implements AgentHealthInvoker {

    private static final Logger log = LoggerFactory.getLogger(RestAgentHealthInvoker.class);
    private final WebClient webClient;

    public RestAgentHealthInvoker(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Boolean> ping(String endpoint) {
        return webClient.get()
                .uri(endpoint + "/ping")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofMillis(500))  // 500ms timeout 적용
                .map(response -> true)
                .doOnError(e -> log.warn("Health check failed for {}: {}", endpoint, e.toString()))
                .onErrorReturn(false);
    }
}
