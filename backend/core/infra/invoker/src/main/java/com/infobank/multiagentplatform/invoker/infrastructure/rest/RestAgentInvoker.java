package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.domain.agent.task.AgentRequest;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestAgentInvoker implements AgentInvoker {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AgentResult invoke(AgentCallTask task) {
//        context.log("Invoking REST agent: " + task.getAgentId());

        try {
            AgentRequest request = new AgentRequest(task.getPayload()); // 내부 생성
            String requestBody = objectMapper.writeValueAsString(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    task.getEndpoint(), entity, String.class);

//            context.recordCall(task.getAgentId(), true, "Success");
            return new AgentResult(response.getBody(), null);

        } catch (Exception e) {
//            context.recordCall(task.getAgentId(), false, "REST 실패: " + e.getMessage());
            throw new RuntimeException("REST agent 호출 실패", e);
        }
    }
}