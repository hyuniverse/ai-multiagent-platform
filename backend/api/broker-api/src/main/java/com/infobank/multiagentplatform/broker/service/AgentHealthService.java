package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Service
public class AgentHealthService {

    private final AgentRepository agentRepository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentHealthChecker agentHealthChecker;

    /**
     * JPA(블로킹) 호출을 Reactor로 감싸서 boundedElastic 스레드 풀에서 실행합니다.
     */
    public Mono<Void> updateAllSnapshots() {
        return Flux.fromIterable(agentRepository.findAll())
                .flatMap(agent ->
                                Mono.fromCallable(() ->
                                                agentHealthChecker.isReachable(agent.getProtocol(), agent.getEndpoint()))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .flatMap(reachable -> Mono.fromRunnable(() -> {
                                            AgentSnapshotEntity snapshot = snapshotRepository.findById(agent.getUuid())
                                                    .orElseThrow(() -> new IllegalStateException("Snapshot missing: " + agent.getUuid()));
                                            snapshot.updateReachable(reachable);
                                            snapshotRepository.save(snapshot);
                                        }).subscribeOn(Schedulers.boundedElastic()))
                        , /*동시성*/ 10)
                .then();
    }
}
