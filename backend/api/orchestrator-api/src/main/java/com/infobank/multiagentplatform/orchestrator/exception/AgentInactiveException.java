package com.infobank.multiagentplatform.orchestrator.exception;

public class AgentInactiveException extends RuntimeException {
    public AgentInactiveException(String agentId) {
        super("Agent is inactive: " + agentId);
    }
}