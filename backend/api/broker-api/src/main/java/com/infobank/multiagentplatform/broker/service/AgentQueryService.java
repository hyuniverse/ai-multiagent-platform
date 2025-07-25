package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.mapper.AgentMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class AgentQueryService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentMapper mapper;

    public List<AgentDetailResponse> getAllAgentDetails() {
        List<AgentEntity> agents = repository.findAll();
        List<AgentSnapshotEntity> snapshots = snapshotRepository.findAll();

        // UUID → SnapshotEntity 매핑
        Map<String, AgentSnapshotEntity> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(AgentSnapshotEntity::getUuid, s -> s));

        return agents.stream()
                .map(agent -> {
                    String uuid = agent.getUuid();
                    AgentMetadata metadata = mapper.toMetadata(agent);
                    AgentSnapshot snapshot = mapper.toSnapshot(snapshotMap.get(uuid));
                    return AgentDetailResponse.of(uuid, metadata, snapshot);
                })
                .collect(Collectors.toList());
    }

    public AgentDetailResponse getAgentDetails(String uuid) {
        AgentEntity agentEntity = repository.findById(uuid).orElse(null);
        AgentSnapshotEntity agentSnapshotEntity = snapshotRepository.findById(uuid).orElse(null);
        if (agentEntity == null || agentSnapshotEntity == null) {
            // thorw Exception
        }
        AgentMetadata metadata = mapper.toMetadata(agentEntity);
        AgentSnapshot snapshot = mapper.toSnapshot(agentSnapshotEntity);
        return AgentDetailResponse.of(uuid, metadata, snapshot);
    }

    public List<AgentSummaryResponse> getAvailableAgentSummaries() {
        // 1) 모든 에이전트 메타데이터 조회
        List<AgentEntity> agents = repository.findAll();

        // 2) UUID 리스트로 해당 스냅샷만 일괄 조회
        List<String> uuids = agents.stream()
                .map(AgentEntity::getUuid)
                .collect(Collectors.toList());
        List<AgentSnapshotEntity> snapshots = snapshotRepository.findAllByUuidIn(uuids);
        Map<String, AgentSnapshotEntity> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(AgentSnapshotEntity::getUuid, Function.identity()));

        // 3) reachable한 에이전트만 필터링 후 요약 DTO로 매핑
        return agents.stream()
                .filter(agent -> {
                    AgentSnapshotEntity se = snapshotMap.get(agent.getUuid());
                    return se.isReachable();
                })
                .map(agent -> {
                    String uuid = agent.getUuid();
                    AgentMetadata metadata = mapper.toMetadata(agent);
                    return AgentSummaryResponse.of(uuid, metadata);
                })
                .collect(Collectors.toList());
    }

    public List<AgentDetailResponse> getAgentDetailsBatch(List<String> uuids) {
        List<AgentEntity> agents = repository.findAllByUuidIn(uuids);
        List<AgentSnapshotEntity> snapshots = snapshotRepository.findAllByUuidIn(uuids);

        Map<String, AgentSnapshotEntity> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(AgentSnapshotEntity::getUuid, Function.identity()));

        return agents.stream()
                .map(agent -> {
                    AgentMetadata metadata = mapper.toMetadata(agent);
                    AgentSnapshotEntity snapshotEntity = snapshotMap.get(agent.getUuid());
                    AgentSnapshot snapshot = mapper.toSnapshot(snapshotEntity);
                    return AgentDetailResponse.of(agent.getUuid(), metadata, snapshot);
                })
                .toList();
    }
}
