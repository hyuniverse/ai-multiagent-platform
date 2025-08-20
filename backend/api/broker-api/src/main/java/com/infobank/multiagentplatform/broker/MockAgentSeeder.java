package com.infobank.multiagentplatform.broker;

import com.infobank.multiagentplatform.broker.service.AgentService;
import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MockAgentSeeder implements CommandLineRunner {

    private final AgentService agentService;
    private final AgentRepository agentRepository;

    @Override
    public void run(String... args) {
        String mockEndpoint = "http://wiremock:8080";
        if (!agentRepository.existsByEndpoint(mockEndpoint)) {
            AgentRegisterServiceRequest mockAgent = AgentRegisterServiceRequest.builder()
                    .name("mock-summary-agent")
                    .type("SUMMARY")
                    .protocol(ProtocolType.REST)
                    .endpoint(mockEndpoint)
                    .hasMemory(false)
                    .inputTypes(List.of("TEXT"))
                    .outputTypes(List.of("TEXT"))
                    .description("A mock agent for summarizing text.")
                    .build();

            agentService.registerAgent(mockAgent);
        }
    }
}