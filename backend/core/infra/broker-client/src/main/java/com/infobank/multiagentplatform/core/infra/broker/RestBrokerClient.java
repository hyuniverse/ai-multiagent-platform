package com.infobank.multiagentplatform.core.infra.broker;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * BrokerClient 인터페이스의 REST 구현체
 * API 브로커 모듈에 위치하며 실제 HTTP 호출을 담당
 */
@Component
@RequiredArgsConstructor
public class RestBrokerClient implements BrokerClient {
    private final RestTemplate restTemplate;

    /** 브로커 서비스 베이스 URL (application.yml에 설정) */
    @Value("${broker.service.url}")
    private String brokerServiceUrl;

    @Override
    public AgentMetadata getAgentMetadata(String agentId) {
        String url = brokerServiceUrl + "/agents/" + agentId + "/metadata";
        ResponseEntity<AgentMetadata> resp =
                restTemplate.getForEntity(url, AgentMetadata.class);
        return resp.getBody();
    }

    @Override
    public List<AgentMetadata> getAgentMetadataBatch(List<String> agentIds) {
        String url = brokerServiceUrl + "/agents/metadata/batch";
        HttpEntity<List<String>> request = new HttpEntity<>(agentIds);
        ResponseEntity<List<AgentMetadata>> resp = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<AgentMetadata>>() {}
        );
        return resp.getBody();
    }

    @Override
    public List<AgentSummary> getAgentSummaries() {
        String url = brokerServiceUrl + "/agents/summaries";
        return restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<AgentSummary>>() {}
        ).getBody();
    }
}

// application.yml 예시 설정
// broker.service.url: http://localhost:8081/api/broker
