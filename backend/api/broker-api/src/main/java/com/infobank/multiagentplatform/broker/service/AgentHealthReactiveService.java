package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AgentHealthReactiveService {

    private final AgentHealthChecker healthChecker;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentRepository agentRepository;

    @Value("${orchestrator.timeouts.agent-health:500ms}")
    private Duration agentHealthTimeout;

    public Mono<Void> checkAllAgentsHealth() {
        Mono<Map<String, AgentEntity>> agentMapMono =
                Flux.defer(() -> agentRepository.findAll())
                        .collectMap(AgentEntity::getUuid);

        Mono<Map<String, AgentSnapshotEntity>> snapshotMapMono =
                Flux.defer(() -> snapshotRepository.findAll())
                        .collectMap(AgentSnapshotEntity::getUuid);

        return Mono.zip(agentMapMono, snapshotMapMono)
                .flatMapMany(tuple -> {
                    Map<String, AgentEntity> agentMap = tuple.getT1();
                    Map<String, AgentSnapshotEntity> snapshotMap = tuple.getT2();
                    return Flux.fromIterable(agentMap.values())
                            .flatMap(agent -> {
                                AgentSnapshotEntity snapshot = snapshotMap.get(agent.getUuid());
                                if (snapshot == null) {
                                    return Mono.empty();
                                }
                                return healthChecker.isReachable(ProtocolType.valueOf(agent.getProtocol().toUpperCase()), agent.getEndpoint())
                                        .map(reachable -> {
                                            snapshot.updateReachable(reachable);
                                            return snapshot;
                                        })
                                        .onErrorResume(ex -> Mono.empty());
                            });
                })
                .collectList()
                .flatMap(updatedSnapshots -> {
                    if (updatedSnapshots.isEmpty()) {
                        return Mono.empty();
                    }
                    return snapshotRepository.saveAllWithEnumCast(updatedSnapshots)
                            .then();
                })
                .timeout(agentHealthTimeout)
                .then();
    }

    public Mono<Void> checkAndUpdate(String uuid, ProtocolType protocol, String endpoint) {
        return healthChecker.isReachable(protocol, endpoint)
                .flatMap(reachable ->
                        Mono.defer(() -> snapshotRepository.findById(uuid))
                                .switchIfEmpty(Mono.error(new IllegalStateException("No snapshot for: " + uuid)))
                                .flatMap(snapshot -> {
                                    snapshot.updateReachable(reachable);
                                    return Mono.defer(() -> snapshotRepository.saveWithEnumCast(snapshot));
                                })
                )
                .timeout(agentHealthTimeout)
                .onErrorResume(ex -> Mono.empty())
                .then();
    }
}