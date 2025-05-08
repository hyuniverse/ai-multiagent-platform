package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.broker.service.AgentHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HealthCheckRunner {

    private final AgentHealthService agentHealthService;

    @Scheduled(fixedRate = 60000) // 매 60초
    public void run() {
        agentHealthService.updateAllSnapshots();
    }
}
