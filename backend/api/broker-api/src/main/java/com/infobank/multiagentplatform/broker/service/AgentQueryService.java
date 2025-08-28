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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AgentQueryService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentMapper mapper;

    public Mono<List<AgentDetailResponse>> getAllAgentDetails() {
        Mono<Map<String, AgentSnapshotEntity>> snapshotMapMono =
                snapshotRepository.findAll()
                        .collectMap(AgentSnapshotEntity::getUuid);

        return snapshotMapMono.flatMapMany(snapshotMap ->
                repository.findAll()
                        .map(agent -> toAgentDetailResponse(agent, snapshotMap.get(agent.getUuid())))
        ).collectList();
    }

    public Mono<AgentDetailResponse> getAgentDetails(String uuid) {
        Mono<AgentEntity> agentMono = repository.findById(uuid);
        Mono<AgentSnapshotEntity> snapshotMono = snapshotRepository.findById(uuid);

        return Mono.zip(agentMono, snapshotMono)
                .map(tuple -> toAgentDetailResponse(tuple.getT1(), tuple.getT2()))
                .switchIfEmpty(Mono.error(new RuntimeException("Agent not found with uuid: " + uuid)));
    }

    public Mono<List<AgentSummaryResponse>> getAvailableAgentSummaries() {
        return snapshotRepository.findAvailableAgents()
                .map(agent -> {
                    AgentMetadata metadata = mapper.toMetadata(agent);
                    return AgentSummaryResponse.of(agent.getUuid(), metadata);
                })
                .collectList();
    }

    public Mono<List<AgentDetailResponse>> getAgentDetailsBatch(List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }

        Mono<Map<String, AgentSnapshotEntity>> snapshotMapMono =
                snapshotRepository.findAllByUuidIn(uuids)
                        .collectMap(AgentSnapshotEntity::getUuid);

        return snapshotMapMono.flatMapMany(snapshotMap ->
                repository.findAllByUuidIn(uuids)
                        .map(agent -> toAgentDetailResponse(agent, snapshotMap.get(agent.getUuid())))
        ).collectList();
    }

    private AgentDetailResponse toAgentDetailResponse(AgentEntity agent, AgentSnapshotEntity snapshotEntity) {
        AgentMetadata metadata = mapper.toMetadata(agent);
        AgentSnapshot snapshot = (snapshotEntity != null)
                ? mapper.toSnapshotWithLogging(snapshotEntity)
                : AgentSnapshot.builder().build();
        return AgentDetailResponse.of(agent.getUuid(), metadata, snapshot);
    }
}