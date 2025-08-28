package com.infobank.multiagentplatform.domain.agent.entity;

import com.infobank.multiagentplatform.commons.domain.TimeBaseEntity;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "agent_snapshot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class AgentSnapshotEntity extends TimeBaseEntity {

    @Id
    private String uuid;

    private AgentStatus status;

    private boolean reachable;

    @Column("request_count")
    private int requestCount;

    @Builder
    private AgentSnapshotEntity(String uuid, AgentStatus status, boolean reachable, int requestCount) {
        this.uuid = uuid;
        this.status = status;
        this.reachable = reachable;
        this.requestCount = requestCount;
    }

    /**
     * Agent Register 시 최초 생성
     * @param uuid
     * @return
     */
    public static AgentSnapshotEntity of(String uuid) {
        return AgentSnapshotEntity.builder()
                .uuid(uuid)
                .status(AgentStatus.INACTIVE)
                .reachable(false)
                .requestCount(0)
                .build();
    }

    public void updateReachable(boolean reachable) {
        this.reachable = reachable;
        this.status = reachable ? AgentStatus.ACTIVE : AgentStatus.INACTIVE;
    }

    public AgentStatus getStatusAsEnum() {
        return status;
    }
}
