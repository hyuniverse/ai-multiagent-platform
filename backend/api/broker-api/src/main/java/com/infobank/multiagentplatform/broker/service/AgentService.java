package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.broker.service.response.AgentDetailResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.mapper.AgentMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.domain.agent.processor.AgentPostProcessor;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.validator.AgentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class AgentService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentValidator validator;
    private final AgentPostProcessor postProcessor;
    private final AgentMapper mapper;

    public AgentRegisterResponse registerAgent(AgentRegisterServiceRequest request) {
        AgentMetadata metadata = request.toMetadata();

        validator.validate(metadata);

        UUID uuid = UUID.randomUUID();
        AgentEntity entity = AgentEntity.from(metadata,uuid.toString());
        repository.save(entity);

        postProcessor.afterRegister(metadata);

        return AgentRegisterResponse.of(metadata, uuid.toString());
    }

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


}
