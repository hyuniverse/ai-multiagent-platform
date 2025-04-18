package com.infobank.multiagentplatform.domain.agent.type.enumtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProtocolType {
    REST,
    GRPC,
    MCP,
    LEG;

    @JsonCreator
    public static ProtocolType from(String key) {
        return ProtocolType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
