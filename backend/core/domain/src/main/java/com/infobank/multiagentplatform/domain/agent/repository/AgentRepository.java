package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;

public interface AgentRepository extends R2dbcRepository<AgentEntity, String>, AgentRepositoryCustom {
    Mono<Boolean> existsByEndpoint(String endpoint);

    Flux<AgentEntity> findAllByUuidIn(Collection<String> uuids);

}