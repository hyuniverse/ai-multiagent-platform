package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AgentHealthService {

    private final AgentRepository agentRepository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentHealthChecker agentHealthChecker;

    public void updateAllSnapshots() {
        List<AgentEntity> agents = agentRepository.findAll();
        for (AgentEntity agent : agents) {
            boolean reachable = agentHealthChecker.isReachable(agent.getProtocol(), agent.getEndpoint());
            AgentSnapshotEntity snapshot = snapshotRepository.findById(agent.getUuid())
                    .orElseThrow(() -> new IllegalStateException("AgentSnapshot not found for uuid: " + agent.getUuid()));
            snapshot.updateReachable(reachable);
            snapshotRepository.save(snapshot);
        }
    }
}
