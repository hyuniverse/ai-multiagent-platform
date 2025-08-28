package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.broker.service.request.AgentUpdateServiceRequest;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentUpdateResponse;
import com.infobank.multiagentplatform.commons.exception.EntityNotFoundException;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.processor.AgentPostProcessor;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.validator.AgentValidator;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AgentService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentValidator validator;
    private final AgentPostProcessor postProcessor;
    private final AgentHealthReactiveService healthReactiveService;

    @Timed(value = "agent.register.time", description = "Time taken to register agent")
    public Mono<AgentRegisterResponse> registerAgent(AgentRegisterServiceRequest request) {

        AgentMetadata metadata = request.toMetadata();

        return Mono.fromCallable(() -> {
                    AgentEntity entity = AgentEntity.create(metadata);
                })
                .flatMap(entity -> {
                    return repository.saveWithEnumCast(entity)
                            .doOnError(error -> log.error("AgentEntity 저장 실패: {}", error.getMessage(), error));
                })
                .flatMap(savedEntity -> {
                    AgentSnapshotEntity snapshot = AgentSnapshotEntity.of(savedEntity.getUuid());
                    return snapshotRepository.saveWithEnumCast(snapshot)
                            .thenReturn(savedEntity);
                })
                .doOnSuccess(savedEntity -> {
                    postProcessor.afterRegister(metadata);
                })
                .flatMap(savedEntity -> {
                    return healthReactiveService.checkAndUpdate(
                            savedEntity.getUuid(),
                            metadata.getProtocol(),
                            metadata.getEndpoint()
                    ).thenReturn(savedEntity);
                })
                .map(savedEntity -> {
                    log.info("응답 생성: {}", savedEntity.getUuid());
                    return AgentRegisterResponse.of(metadata, savedEntity.getUuid());
                })
                .doOnError(error -> log.error("=== AgentService.registerAgent 실패 ===: {}", error.getMessage(), error))
                .doOnSuccess(response -> log.info("=== AgentService.registerAgent 완료 ===: {}", response.getUuid()));
    }

    @Timed(value = "agent.update.time", description = "Time taken to update agent")
    public Mono<AgentUpdateResponse> updateAgent(String uuid, AgentUpdateServiceRequest request) {

        AgentMetadata metadata = request.toMetadata();

        return repository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("에이전트를 찾을 수 없습니다: " + uuid)))
                .flatMap(existingEntity -> {
                    AgentEntity updatedEntity = existingEntity.updateWith(metadata);
                    return repository.saveWithEnumCast(updatedEntity)
                            .doOnSuccess(saved -> log.info("업데이트된 엔티티 저장 완료: uuid={}, hasMemory={}, memoryType={}", 
                                saved.getUuid(), saved.isHasMemory(), saved.getMemoryType()))
                            .doOnError(error -> log.error("업데이트된 엔티티 저장 실패: {}", error.getMessage(), error));
                })
                .flatMap(savedEntity -> {
                    return healthReactiveService
                            .checkAndUpdate(savedEntity.getUuid(), metadata.getProtocol(), metadata.getEndpoint())
                            .thenReturn(savedEntity);
                })
                .doOnSuccess(savedEntity -> {
                    postProcessor.afterUpdate(metadata);
                })
                .map(savedEntity -> {
                    return AgentUpdateResponse.of(metadata, savedEntity.getUuid());
                })
                .doOnError(error -> log.error("=== AgentService.updateAgent 실패 ===: {}", error.getMessage(), error))
                .doOnSuccess(response -> log.info("=== AgentService.updateAgent 완료 ===: {}", response.getUuid()));
    }

    @Timed(value = "agent.delete.time", description = "Time taken to delete agent")
    public Mono<Void> deleteAgent(String uuid) {
        return repository.existsById(uuid)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new EntityNotFoundException("에이전트를 찾을 수 없습니다: " + uuid));
                    }
                    return snapshotRepository.deleteById(uuid)
                            .then(repository.deleteById(uuid));
                })
                .doOnSuccess(v -> postProcessor.afterDelete(uuid));
    }
}