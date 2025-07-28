package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component

public class RestAgentHealthInvoker implements AgentHealthInvoker {

    private static final Logger log = LoggerFactory.getLogger(RestAgentHealthInvoker.class);
    private final WebClient webClient;

    public RestAgentHealthInvoker(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public boolean ping(String endpoint) {
        try {
            webClient.get()
                    .uri(endpoint + "/ping")
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            log.warn("Health check failed for {}: {}", endpoint, e.toString());
            return false;
        }
    }
}
