package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AgentRepositoryCustomImpl implements AgentRepositoryCustom {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<AgentEntity> saveWithEnumCast(AgentEntity entity) {
        String sql = """
            INSERT INTO agents (uuid, name, endpoint, type, protocol, has_memory, memory_type, description, input_types, output_types)
            VALUES ($1, $2, $3, $4, $5::protocol_type, $6, $7, $8, $9, $10)
            RETURNING uuid
            """;

        log.info("Executing custom SQL with enum cast for protocol: {}", entity.getProtocol());

        return databaseClient.sql(sql)
                .bind("$1", entity.getUuid())
                .bind("$2", entity.getName())
                .bind("$3", entity.getEndpoint())
                .bind("$4", entity.getType())
                .bind("$5", entity.getProtocol()) // PostgreSQL will cast this to protocol_type
                .bind("$6", entity.isHasMemory())
                .bind("$7", entity.getMemoryType() != null ? entity.getMemoryType() : "")
                .bind("$8", entity.getDescription() != null ? entity.getDescription() : "")
                .bind("$9", entity.getInputTypes() != null ? entity.getInputTypes().toArray(new String[0]) : new String[0])
                .bind("$10", entity.getOutputTypes() != null ? entity.getOutputTypes().toArray(new String[0]) : new String[0])
                .map(row -> row.get("uuid", String.class))
                .one()
                .map(uuid -> entity.toBuilder().uuid(uuid).build())
                .doOnSuccess(saved -> log.info("Successfully inserted agent with UUID: {}", saved.getUuid()))
                .doOnError(error -> log.error("Failed to insert agent: {}", error.getMessage(), error));
    }
}
