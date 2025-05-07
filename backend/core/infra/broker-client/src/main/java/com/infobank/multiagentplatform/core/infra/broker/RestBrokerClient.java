package com.infobank.multiagentplatform.core.infra.broker;


import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentBatchRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestBrokerClient implements BrokerClient {

    private final RestTemplate restTemplate;

    @Value("${broker.service.url}")
    private String brokerServiceUrl;

    @Override
    public AgentDetailResponse getAgentMetadata(String agentId) {
        String url = brokerServiceUrl + "/agents/" + agentId;
        ResponseEntity<ApiResponse<AgentDetailResponse>> resp =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ApiResponse<AgentDetailResponse>>() {}
                );
        return resp.getBody().getData();
    }

    @Override
    public List<AgentDetailResponse> getAgentMetadataBatch(List<String> agentIds) {
        String url = brokerServiceUrl + "/agents/batch";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AgentBatchRequest> req = new HttpEntity<>(new AgentBatchRequest(agentIds), headers);

        ResponseEntity<ApiResponse<List<AgentDetailResponse>>> resp =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        req,
                        new ParameterizedTypeReference<ApiResponse<List<AgentDetailResponse>>>() {}
                );
        return resp.getBody().getData();
    }

    @Override
    public List<AgentSummaryResponse> getAgentSummaries() {
        String url = brokerServiceUrl + "/agents/summaries";
        ResponseEntity<ApiResponse<List<AgentSummaryResponse>>> resp =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ApiResponse<List<AgentSummaryResponse>>>() {}
                );
        return resp.getBody().getData();
    }
}
