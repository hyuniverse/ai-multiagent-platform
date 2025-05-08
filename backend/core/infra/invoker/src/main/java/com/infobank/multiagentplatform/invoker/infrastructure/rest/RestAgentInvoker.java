package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * REST 기반 AgentInvoker 구현체 (WebClient 사용)
 */
@Component
public class RestAgentInvoker implements AgentInvoker {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public RestAgentInvoker(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public AgentInvocationResponse invoke(AgentInvocationRequest request) {
        try {
            // 1) HTTP 호출 및 원본 응답 수신
            String raw = webClient.post()
                    .uri(request.getEndpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp -> resp.createException().flatMap(Mono::error))
                    .bodyToMono(String.class)
                    .block();

            // 2) JSON 파싱
            JsonNode parsed = objectMapper.readTree(raw);

            // 3) DTO 반환
            return AgentInvocationResponse.of(raw, parsed);

        } catch (Exception e) {
            throw new RuntimeException("REST agent 호출 실패", e);
        }
    }
}