package com.infobank.multiagentplatform.domain.agent.entity;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agent_snapshots")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSnapshotEntity {

    @Id
    private String uuid; // AgentEntity.uuid와 동일한 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status;

    @Column(nullable = false)
    private boolean reachable;

    @Column(nullable = false)
    private long lastUpdatedTime;

    @Column(nullable = false)
    private int requestCount;

}
