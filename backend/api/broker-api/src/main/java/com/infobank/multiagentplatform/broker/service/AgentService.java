package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.broker.service.request.AgentUpdateServiceRequest;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentUpdateResponse;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.processor.AgentPostProcessor;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.validator.AgentValidator;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AgentService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentValidator validator;
    private final AgentPostProcessor postProcessor;
    private final AgentHealthReactiveService healthReactiveService;

    @Timed(value = "agent.register.time", description = "Time taken to register agent")
    public AgentRegisterResponse registerAgent(AgentRegisterServiceRequest request) {
        AgentMetadata metadata = request.toMetadata();

        validator.validateForCreate(metadata);

        AgentEntity entity = AgentEntity.from(metadata);
        AgentEntity savedEntity = repository.save(entity);

        AgentSnapshotEntity snapshot = AgentSnapshotEntity.of(savedEntity.getUuid());
        snapshotRepository.save(snapshot);

        postProcessor.afterRegister(metadata);

        healthReactiveService
                .checkAndUpdate(savedEntity.getUuid(),
                        metadata.getProtocol(),
                        metadata.getEndpoint())
                .subscribe();

        return AgentRegisterResponse.of(metadata, savedEntity.getUuid());
    }

    @Timed(value = "agent.update.time", description = "Time taken to update agent")
    public AgentUpdateResponse updateAgent(String uuid, AgentUpdateServiceRequest request) {
        AgentMetadata metadata = request.toMetadata();

        validator.validateForUpdate(metadata);

        AgentEntity existing = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("에이전트를 찾을 수 없습니다: " + uuid));
        // 엔티티 필드 갱신
        existing.updateFrom(metadata);
        AgentEntity saved = repository.save(existing);

        postProcessor.afterUpdate(metadata);

        healthReactiveService
                .checkAndUpdate(saved.getUuid(), metadata.getProtocol(), metadata.getEndpoint())
                .subscribe();

        return AgentUpdateResponse.of(metadata, saved.getUuid());
    }

    @Timed(value = "agent.delete.time", description = "Time taken to delete agent")
    public void deleteAgent(String uuid) {
        if (!repository.existsById(uuid)) {
            throw new EntityNotFoundException("에이전트를 찾을 수 없습니다: " + uuid);
        }
        // 스냅샷 먼저 삭제
        snapshotRepository.deleteById(uuid);
        repository.deleteById(uuid);
        postProcessor.afterDelete(uuid);
    }

}
