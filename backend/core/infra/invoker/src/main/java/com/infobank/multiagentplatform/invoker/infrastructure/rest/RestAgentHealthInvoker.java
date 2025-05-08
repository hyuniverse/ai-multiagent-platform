package com.infobank.multiagentplatform.invoker.infrastructure.rest;

import com.infobank.multiagentplatform.invoker.domain.AgentHealthInvoker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestAgentHealthInvoker implements AgentHealthInvoker {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean ping(String endpoint) {
        try {
            restTemplate.getForEntity(endpoint + "/ping", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
