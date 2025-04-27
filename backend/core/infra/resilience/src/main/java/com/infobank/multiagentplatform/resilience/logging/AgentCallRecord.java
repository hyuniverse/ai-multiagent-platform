package com.infobank.multiagentplatform.resilience.logging;

import java.time.Instant;

public class AgentCallRecord {

    private final String agentId;
    private final boolean success;
    private final String message;
    private final Instant timestamp;

    public AgentCallRecord(String agentId, boolean success, String message, Instant timestamp) {
        this.agentId = agentId;
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getAgentId() { return agentId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
