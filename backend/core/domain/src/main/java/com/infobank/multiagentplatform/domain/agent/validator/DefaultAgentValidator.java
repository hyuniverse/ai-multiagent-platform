package com.infobank.multiagentplatform.domain.agent.validator;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class DefaultAgentValidator implements AgentValidator {

    private final AgentRepository agentRepository;

    @Override
    public void validate(AgentMetadata metadata) {
        if (agentRepository.existsByEndpoint(metadata.getEndpoint())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "중복된 endpoint입니다: " + metadata.getEndpoint()
            );
        }

        // 앞으로 정책 기반 유효성 검사만 추가
    }
}
