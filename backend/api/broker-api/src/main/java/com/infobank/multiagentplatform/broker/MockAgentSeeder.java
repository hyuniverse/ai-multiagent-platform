package com.infobank.multiagentplatform.broker;

import com.infobank.multiagentplatform.broker.service.AgentService;
import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockAgentSeeder implements CommandLineRunner {

    private final AgentService agentService;
    private final AgentRepository agentRepository;

    @Override
    public void run(String... args) {
        log.info("=== MockAgentSeeder started ===");
        String mockEndpoint = "http://wiremock:8080";

        try {
            agentRepository.existsByEndpoint(mockEndpoint)
                    .doOnSubscribe(subscription -> log.info("Checking if mock agent exists at endpoint: {}", mockEndpoint))
                    .flatMap(exists -> {
                        log.info("Mock agent exists check result: {}", exists);
                        if (!exists) {
                            log.info("Mock agent not found, registering new mock agent...");
                            AgentRegisterServiceRequest mockAgent = AgentRegisterServiceRequest.builder()
                                    .name("mock-summary-agent")
                                    .type("SUMMARY")
                                    .protocol(ProtocolType.REST)
                                    .endpoint(mockEndpoint)
                                    .hasMemory(false)
                                    .memoryType("")
                                    .inputTypes(List.of("TEXT"))
                                    .outputTypes(List.of("TEXT"))
                                    .description("A mock agent for summarizing text.")
                                    .build();

                            log.info("Attempting to register agent: {}", mockAgent.getName());
                            return agentService.registerAgent(mockAgent)
                                    .doOnSuccess(result -> log.info("Agent registration successful: {}", result))
                                    .doOnError(error -> log.error("Agent registration failed", error))
                                    .then(Mono.just("success"));
                        } else {
                            log.info("Mock agent already exists, skipping registration.");
                            return Mono.just("exists");
                        }
                    })
                    .doOnSuccess(result -> log.info("Mock agent seeding completed successfully: {}", result))
                    .doOnError(error -> log.error("Mock agent seeding failed", error))
                    .onErrorResume(error -> {
                        log.error("Error in MockAgentSeeder, but continuing", error);
                        return Mono.just("error");
                    })
                    .block();

            log.info("=== MockAgentSeeder completed ===");
        } catch (Exception e) {
            log.error("Exception in MockAgentSeeder", e);
        }
    }

}