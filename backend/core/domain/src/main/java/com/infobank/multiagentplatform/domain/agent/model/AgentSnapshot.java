package com.infobank.multiagentplatform.domain.agent.model;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSnapshot {

    private String uuid;
    private AgentStatus status;
    private boolean reachable;
    private long lastUpdatedTime;
    private int requestCount;
}

