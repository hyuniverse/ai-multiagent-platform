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
        // 공통 커넥터·타임아웃·MDC 필터가 적용된 builder
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
                // 필요하다면 개별 호출 타임아웃 유지
                .timeout(Duration.ofMillis(30000))
                .flatMap(raw ->
                        Mono.fromCallable(() -> objectMapper.readTree(raw))
                                .map(parsed -> AgentInvocationResponse.of(raw, parsed))
                );
    }

    // fallbackMethod 시그니처: 원본 파라미터 + Throwable, Mono 리턴
    private Mono<AgentInvocationResponse> fallbackInvoke(AgentInvocationRequest request, Throwable ex) {
        // 여기에 대체 응답 로직을 작성
        return Mono.just(
                AgentInvocationResponse.failure("fallback", ex.getClass().getSimpleName())
        );
    }
}