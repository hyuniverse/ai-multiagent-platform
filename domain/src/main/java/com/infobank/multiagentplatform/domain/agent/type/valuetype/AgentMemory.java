package com.infobank.multiagentplatform.domain.agent.type.valuetype;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Builder
public class AgentMemory {

    @Column(nullable = false)
    private final Boolean hasMemory;

    private final String memoryType;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(hasMemory);
    }

    public static AgentMemory disabled() {
        return new AgentMemory(false, null);
    }

    public static AgentMemory of(String memoryType) {
        return new AgentMemory(true, memoryType);
    }
}
