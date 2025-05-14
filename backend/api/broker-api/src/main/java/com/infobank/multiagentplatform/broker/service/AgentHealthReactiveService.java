package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j
public class AgentHealthReactiveService {

    private final AgentHealthChecker healthChecker;
    private final AgentSnapshotRepository snapshotRepository;

    public Mono<Void> checkAndUpdate(String uuid, ProtocolType protocol, String endpoint) {
        return Mono.fromCallable(() ->
                        healthChecker.isReachable(protocol, endpoint))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(reachable -> Mono.fromRunnable(() -> {
                    AgentSnapshotEntity snap = snapshotRepository.findById(uuid)
                            .orElseThrow(() -> new IllegalStateException("No snapshot for: " + uuid));
                    snap.updateReachable(reachable);
                    snapshotRepository.save(snap);
                    log.info("Reactive health check: {} → {}", uuid, reachable);
                }).subscribeOn(Schedulers.boundedElastic()))
                // 재시도·백오프
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)))
                .timeout(Duration.ofSeconds(5))
                // 실패 시 빈 결과로 처리
                .onErrorResume(ex -> {
                    log.warn("Reactive health check failed for {}: {}", uuid, ex.toString());
                    return Mono.empty();
                }).then();
    }
}
