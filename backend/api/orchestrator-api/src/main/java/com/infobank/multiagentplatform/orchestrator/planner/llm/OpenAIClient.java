package com.infobank.multiagentplatform.orchestrator.planner.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.config.LLMClientProperties;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.planner.PlanJsonParser;
import com.infobank.multiagentplatform.orchestrator.planner.PromptBuilder;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * OpenAI API 호출 및 ExecutionPlan 수립 구현체
 */
@Component
@Qualifier("openAIClient")
public class OpenAIClient implements LLMClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String model;
    private final PromptBuilder promptBuilder;
    private final PlanJsonParser planJsonParser;

    public OpenAIClient(RestTemplateBuilder builder,
                        LLMClientProperties props,
                        PromptBuilder promptBuilder,
                        PlanJsonParser planJsonParser) {


        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(60))
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);

        this.restTemplate = builder
                .requestFactory(() -> factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
                .build();

        this.apiUrl = props.getApiUrl();
        this.model = props.getModel();
        this.promptBuilder = promptBuilder;
        this.planJsonParser = planJsonParser;
    }

    @Override
    public ExecutionPlan plan(StandardRequest request, List<AgentSummary> agentSummaries) {
        String prompt = promptBuilder.buildPrompt(request, agentSummaries);
        String content = callOpenAI(prompt);
        System.out.println(content);
        return planJsonParser.parse(content);
    }

    /**
     * OpenAI에 프롬프트를 보내고 응답 텍스트 추출
     */
    private String callOpenAI(String prompt) {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(message)
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(apiUrl, request, JsonNode.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("OpenAI API call failed: " + response.getStatusCode());
        }
        JsonNode choices = response.getBody().get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new IllegalStateException("No choices in OpenAI response");
        }
        return choices.get(0).get("message").get("content").asText();
    }
}
