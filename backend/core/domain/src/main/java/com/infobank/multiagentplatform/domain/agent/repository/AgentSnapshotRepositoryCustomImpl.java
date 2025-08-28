package com.infobank.multiagentplatform.domain.agent.repository;

import com.infobank.multiagentplatform.domain.agent.entity.AgentSnapshotEntity;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class AgentSnapshotRepositoryCustomImpl implements AgentSnapshotRepositoryCustom {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public AgentSnapshotRepositoryCustomImpl(DatabaseClient databaseClient, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.databaseClient = databaseClient;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    @Override
    public Mono<AgentSnapshotEntity> saveWithEnumCast(AgentSnapshotEntity entity) {
        // Check if entity exists using raw SQL to avoid table name issues
        String existsSQL = "SELECT COUNT(*) FROM agent_snapshot WHERE uuid = $1";
        
        return databaseClient.sql(existsSQL)
                .bind("$1", entity.getUuid())
                .fetch()
                .one()
                .map(result -> ((Number) result.get("count")).intValue() > 0)
                .flatMap(exists -> {
                    if (exists) {
                        return updateWithEnumCast(entity);
                    } else {
                        return insertWithEnumCast(entity);
                    }
                });
    }

    @Override
    public Flux<AgentSnapshotEntity> saveAllWithEnumCast(List<AgentSnapshotEntity> entities) {
        return Flux.fromIterable(entities)
                .flatMap(this::saveWithEnumCast);
    }

    private Mono<AgentSnapshotEntity> insertWithEnumCast(AgentSnapshotEntity entity) {
        String sql = """
            INSERT INTO agent_snapshot (uuid, status, reachable, request_count, is_deleted, created_date_time, last_modified_date_time)
            VALUES ($1, $2::agent_status, $3, $4, $5, NOW(), NOW())
            """;

        return databaseClient.sql(sql)
                .bind("$1", entity.getUuid())
                .bind("$2", entity.getStatus().name())
                .bind("$3", entity.isReachable())
                .bind("$4", entity.getRequestCount())
                .bind("$5", entity.isDeleted())
                .then()
                .thenReturn(entity);
    }

    private Mono<AgentSnapshotEntity> updateWithEnumCast(AgentSnapshotEntity entity) {
        String sql = """
            UPDATE agent_snapshot 
            SET status = $1::agent_status, reachable = $2, request_count = $3, last_modified_date_time = NOW()
            WHERE uuid = $4
            """;

        return databaseClient.sql(sql)
                .bind("$1", entity.getStatus().name())
                .bind("$2", entity.isReachable())
                .bind("$3", entity.getRequestCount())
                .bind("$4", entity.getUuid())
                .then()
                .thenReturn(entity);
    }
}
