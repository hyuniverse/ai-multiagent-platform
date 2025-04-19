package com.infobank.multiagentplatform.domain.agent.type.enumtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InputType {
    TEXT,
    IMAGE;

    @JsonCreator
    public static InputType from(String key) {
        return InputType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
