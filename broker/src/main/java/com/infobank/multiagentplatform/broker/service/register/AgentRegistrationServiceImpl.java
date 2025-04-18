package com.infobank.multiagentplatform.broker.service.register;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.mapper.AgentMapper;
import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AgentRegistrationServiceImpl implements AgentRegistrationService {

    private final AgentMapper mapper;
    private final AgentRepository repository;

    @Override
    public void register(AgentMetadata metadata) {
        if (repository.existsById(metadata.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Agent with id '" + metadata.getId() + "' already exists.");
        }

        AgentEntity entity = mapper.toEntity(metadata);
        repository.save(entity);
    }
}
