package com.infobank.multiagentplatform.core.infra.broker;

import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentBatchRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RestBrokerClient implements BrokerClient {

    private final WebClient webClient;

    public RestBrokerClient(WebClient.Builder webClientBuilder,
                            @Value("${broker.service.url}") String brokerServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(brokerServiceUrl)
                .build();
    }

    @Override
    public Mono<List<AgentDetailResponse>> getAgentMetadataBatch(List<String> agentIds) {
        return webClient.post()
                .uri("/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AgentBatchRequest(agentIds))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<AgentDetailResponse>>>() {})
                .handle((resp, sink) -> {
                    if (resp == null || resp.getData() == null) {
                        sink.error(new IllegalStateException("Batch 응답이 null입니다."));
                        return;
                    }
                    sink.next(resp.getData());
                });
    }

    @Override
    public Mono<List<AgentSummaryResponse>> getAgentSummaries() {
        return webClient.get()
                .uri("/summaries")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<AgentSummaryResponse>>>() {})
                .handle((resp, sink) -> {
                    if (resp == null || resp.getData() == null) {
                        sink.error(new IllegalStateException("Agent summaries 응답이 null입니다."));
                        return;
                    }
                    sink.next(resp.getData());
                });
    }
}
