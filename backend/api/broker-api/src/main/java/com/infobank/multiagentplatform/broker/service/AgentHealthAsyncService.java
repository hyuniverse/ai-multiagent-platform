package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AgentHealthAsyncService {

    private final AgentHealthChecker healthChecker;
    private final AgentSnapshotRepository snapshotRepository;

    @Async
    public void checkAndUpdate(String uuid, ProtocolType protocol, String endpoint) {
        try {
            boolean reachable = healthChecker.isReachable(protocol, endpoint);
            AgentSnapshotEntity snapshot = snapshotRepository.findById(uuid)
                    .orElseThrow(() -> new IllegalStateException("No snapshot found for: " + uuid));

            snapshot.updateReachable(reachable);
            snapshotRepository.save(snapshot);

            log.info("Health check complete for {} - reachable: {}", uuid, reachable);
        } catch (Exception e) {
            log.warn("Async health check failed for agent {}: {}", uuid, e.getMessage());
        }
    }
}
