package com.infobank.multiagentplatform.domain.agent.type.enumtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgentStatus {
    ACTIVE,
    INACTIVE,
    FAILED;

    @JsonCreator
    public static AgentStatus from(String key) {
        return AgentStatus.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
