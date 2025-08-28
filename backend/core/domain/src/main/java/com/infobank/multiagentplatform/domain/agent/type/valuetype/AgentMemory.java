package com.infobank.multiagentplatform.domain.agent.type.valuetype;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Builder
public class AgentMemory {

    private final boolean hasMemory;
    private final String memoryType;

    public static AgentMemory of(Boolean hasMemory, String memoryType) {
        if (hasMemory != null && hasMemory && memoryType != null && !memoryType.isBlank()) {
            return new AgentMemory(true, memoryType);
        } else {
            return new AgentMemory(false, null);
        }
    }
}