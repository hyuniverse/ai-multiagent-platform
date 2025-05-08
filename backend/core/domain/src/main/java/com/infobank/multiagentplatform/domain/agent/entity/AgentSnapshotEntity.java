package com.infobank.multiagentplatform.domain.agent.entity;

import com.infobank.multiagentplatform.commons.domain.TimeBaseEntity;
import com.infobank.multiagentplatform.domain.agent.model.AgentSnapshot;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agent_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSnapshotEntity extends TimeBaseEntity {

    @Id
    private String uuid; // AgentEntity.uuid와 동일한 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status;

    @Column(nullable = false)
    private boolean reachable;

    @Column(nullable = false)
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

}
