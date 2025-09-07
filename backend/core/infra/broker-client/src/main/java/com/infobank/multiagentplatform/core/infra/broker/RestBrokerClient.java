package com.infobank.multiagentplatform.core.infra.broker;

import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentBatchRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class RestBrokerClient implements BrokerClient {

    private final WebClient webClient;
    private final Scheduler boundedElasticScheduler;
    private final Duration operatorTimeout;

    public RestBrokerClient(WebClient.Builder webClientBuilder,
                            @Value("${broker.service.url}") String brokerServiceUrl,
                            @Qualifier("boundedElasticScheduler") Scheduler boundedElasticScheduler,
                            @Value("${orchestrator.timeouts.broker-operator:1000ms}") Duration operatorTimeout) {
        this.webClient = webClientBuilder
                .baseUrl(brokerServiceUrl)
                .build();
        this.boundedElasticScheduler = boundedElasticScheduler;
        this.operatorTimeout = operatorTimeout;
    }

    @Override
    @CircuitBreaker(name = "brokerCB", fallbackMethod = "fallbackGetAgentMetadataBatch")
    @Retry(name = "brokerRetry")
    public Mono<List<AgentDetailResponse>> getAgentMetadataBatch(List<String> agentIds) {
        return webClient.post()
                .uri("/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AgentBatchRequest(agentIds))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<AgentDetailResponse>>>() {})
                .timeout(operatorTimeout)
                .doOnError(error -> log.error("배치 조회 실패: {}", error.getMessage(), error))
                .handle((resp, sink) -> {
                    if (resp == null || resp.getData() == null) {
                        sink.error(new IllegalStateException("Batch 응답이 null입니다."));
                        return;
                    }
                    sink.next(resp.getData());
                });
    }

    @Override
    @CircuitBreaker(name = "brokerCB", fallbackMethod = "fallbackGetAgentSummaries")
    @Retry(name = "brokerRetry")
    public Mono<List<AgentSummaryResponse>> getAgentSummaries() {
        return webClient.get()
                .uri("/summaries")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<AgentSummaryResponse>>>() {})
                .timeout(operatorTimeout)
                .doOnError(error -> log.error("요약 정보 조회 실패: {}", error.getMessage(), error))
                .handle((resp, sink) -> {
                    if (resp == null || resp.getData() == null) {
                        sink.error(new IllegalStateException("Agent summaries 응답이 null입니다."));
                        return;
                    }
                    sink.next(resp.getData());
                });
    }

    public Mono<List<AgentDetailResponse>> fallbackGetAgentMetadataBatch(List<String> agentIds, Throwable ex) {
        log.warn("BrokerClient.getAgentMetadataBatch fallback 실행: {}", ex.getMessage());
        return Mono.error(new IllegalStateException("Agent metadata batch 조회 실패: " + ex.getMessage(), ex));
    }

    public Mono<List<AgentSummaryResponse>> fallbackGetAgentSummaries(Throwable ex) {
        log.warn("BrokerClient.getAgentSummaries fallback 실행: {}", ex.getMessage());
        return Mono.error(new IllegalStateException("Agent summaries 조회 실패: " + ex.getMessage(), ex));
    }
}
