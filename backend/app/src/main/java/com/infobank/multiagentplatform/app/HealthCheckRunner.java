package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.broker.service.AgentHealthReactiveService; // 서비스 변경
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

@Slf4j
@RequiredArgsConstructor
@Component
public class HealthCheckRunner {

    private final AgentHealthReactiveService agentHealthReactiveService;

    @Scheduled(fixedRate = 60000, initialDelay = 10000) // 1분에 한 번, 시작 후 10초 뒤에 첫 실행
    public void run() {
        Disposable sub = agentHealthReactiveService
                .checkAllAgentsHealth()
                .doOnError(err ->
                        log.error("HealthCheckRunner encountered an error", err))
                .subscribe();
    }
}