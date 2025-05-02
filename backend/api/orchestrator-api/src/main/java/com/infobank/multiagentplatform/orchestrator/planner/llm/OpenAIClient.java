package com.infobank.multiagentplatform.orchestrator.planner.llm;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * OpenAI API 호출 구현
 */
@Component
public class OpenAIClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String model;

    public OpenAIClient(RestTemplateBuilder builder,
                        @Value("${openai.api.url}") String apiUrl,
                        @Value("${openai.api.key}") String apiKey,
                        @Value("${openai.api.model:gpt-3.5-turbo}") String model) {
        this.restTemplate = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * 프롬프트를 보내고, LLM 응답 메시지(content)만 추출하여 반환
     */
    public String callOpenAI(String prompt) {
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
        if (choices == null || !choices.isArray() || choices.size() == 0) {
            throw new IllegalStateException("No choices in OpenAI response");
        }
        return choices.get(0).get("message").get("content").asText();
    }
}
