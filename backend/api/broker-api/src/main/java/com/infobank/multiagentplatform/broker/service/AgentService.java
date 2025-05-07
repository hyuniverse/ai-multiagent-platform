package com.infobank.multiagentplatform.broker.service;

import com.infobank.multiagentplatform.broker.service.request.AgentRegisterServiceRequest;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.mapper.AgentMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.processor.AgentPostProcessor;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import com.infobank.multiagentplatform.domain.agent.repository.AgentSnapshotRepository;
import com.infobank.multiagentplatform.domain.agent.validator.AgentValidator;
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




}
