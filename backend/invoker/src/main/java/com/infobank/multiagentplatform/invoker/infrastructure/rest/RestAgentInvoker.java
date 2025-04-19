package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.domain.AgentRequest;
import com.infobank.multiagentplatform.invoker.domain.AgentResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestAgentInvoker implements AgentInvoker {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AgentResult invoke(AgentMetadata metadata, AgentRequest request) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    metadata.getEndpoint(), entity, String.class);

            return new AgentResult(response.getBody(), null); // parsedResult는 이후 처리
        } catch (Exception e) {
            throw new RuntimeException("Agent 호출 실패", e);
        }
    }

}