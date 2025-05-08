package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.processor.AgentPostProcessor;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.validator.AgentValidator;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class AgentService {
    private final AgentRepository repository;
    private final AgentSnapshotRepository snapshotRepository;
    private final AgentValidator validator;
    private final AgentPostProcessor postProcessor;
    private final AgentHealthAsyncService healthAsyncService;

    @Timed(value = "agent.register.time", description = "Time taken to register agent")
    public AgentRegisterResponse registerAgent(AgentRegisterServiceRequest request) {
        AgentMetadata metadata = request.toMetadata();

        validator.validate(metadata);

        AgentEntity entity = AgentEntity.from(metadata);
        AgentEntity savedEntity = repository.save(entity);

        AgentSnapshotEntity snapshot = AgentSnapshotEntity.of(savedEntity.getUuid());
        snapshotRepository.save(snapshot);

        postProcessor.afterRegister(metadata);

        healthAsyncService.checkAndUpdate(
                savedEntity.getUuid(),
                metadata.getProtocol(),
                metadata.getEndpoint()
        );

        return AgentRegisterResponse.of(metadata, savedEntity.getUuid());
    }

}
