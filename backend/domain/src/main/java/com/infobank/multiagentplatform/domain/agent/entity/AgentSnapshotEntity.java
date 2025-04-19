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
    private String agentId;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    private boolean reachable;

    private long lastUpdatedTime;

    private int requestCount;
}
