package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.broker.service.AgentHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;


@RequiredArgsConstructor
@Component
public class HealthCheckRunner {

    private final AgentHealthService agentHealthService;

    @Scheduled(fixedRate = 60000)
    public void run() {
        Disposable sub = agentHealthService
                .updateAllSnapshots()
                .doOnError(err ->
                        System.err.println("HealthCheckRunner error: " + err.getMessage()))
                .subscribe();
    }
}
