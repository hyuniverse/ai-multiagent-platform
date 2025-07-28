package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * REST 기반 AgentInvoker 구현체 (WebClient 사용)
 */
@Component
public class RestAgentInvoker implements AgentInvoker {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public RestAgentInvoker(WebClient.Builder webClientBuilder,
                            ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @CircuitBreaker(name = "agent-cb", fallbackMethod = "fallbackInvoke")
    @Bulkhead(name = "agent-bh")
    public Mono<AgentInvocationResponse> invoke(AgentInvocationRequest request) {
        return webClient.post()
                .uri(request.getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(),
                        resp -> resp.createException().flatMap(Mono::error))
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(30000))
                .flatMap(raw ->
                        Mono.fromCallable(() -> objectMapper.readTree(raw))
                                .map(parsed -> AgentInvocationResponse.of(raw, parsed))
                );
    }

    private Mono<AgentInvocationResponse> fallbackInvoke(AgentInvocationRequest request, Throwable ex) {
        return Mono.just(
                AgentInvocationResponse.failure("fallback", ex.getClass().getSimpleName())
        );
    }
}