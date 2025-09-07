package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;

/**
 * REST 기반 AgentInvoker 구현체 (WebClient 사용)
 */
@Component
public class RestAgentInvoker implements AgentInvoker {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Scheduler boundedElasticScheduler;
    private final ReactiveMetricOperator metricOperator;
    private final Duration operatorTimeout;

    public RestAgentInvoker(@Qualifier("agentWebClientBuilder") WebClient.Builder webClientBuilder,
                            ObjectMapper objectMapper,
                            @Qualifier("boundedElasticScheduler") Scheduler boundedElasticScheduler,
                            ReactiveMetricOperator metricOperator,
                            @Value("${orchestrator.timeouts.agent-operator:500ms}") Duration operatorTimeout) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.boundedElasticScheduler = boundedElasticScheduler;
        this.metricOperator = metricOperator;
        this.operatorTimeout = operatorTimeout;
    }

    @CircuitBreaker(name = "agentCB", fallbackMethod = "fallbackInvoke")
    @Bulkhead(name = "agentBH")
    public Mono<AgentInvocationResponse> invoke(AgentInvocationRequest request) {
        Mono<String> httpMono = webClient.post()
                .uri(request.getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(),
                        resp -> resp.createException().flatMap(Mono::error))
                .bodyToMono(String.class)
                .timeout(operatorTimeout)
                .transform(metricOperator.measure("orchestration.executor.task.http"));

        return httpMono
                .flatMap(raw ->
                        Mono.fromCallable(() -> objectMapper.readTree(raw))
                                .subscribeOn(boundedElasticScheduler)
                                .transform(metricOperator.measure("orchestration.executor.task.parse"))
                                .map(parsed -> AgentInvocationResponse.of(raw, parsed))
                );
    }

    private Mono<AgentInvocationResponse> fallbackInvoke(AgentInvocationRequest request, Throwable ex) {
        return Mono.just(
                AgentInvocationResponse.failure("fallback", ex.getClass().getSimpleName())
        );
    }
}