package com.infobank.multiagentplatform.domain.agent.type.enumtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgentType {
    REACT,
    FUNCTION_CALL,
    TOOL_USE,
    CHAIN_OF_THOUGHT,
    PLAN_EXECUTE;

    @JsonCreator
    public static AgentType from(String key) {
        return AgentType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
