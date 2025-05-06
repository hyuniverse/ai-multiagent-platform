package com.infobank.multiagentplatform.domain.agent.exception;

public class AgentEndpointConflictException extends RuntimeException {
    public AgentEndpointConflictException(String endpoint) {
        super("중복된 endpoint입니다: " + endpoint);
    }
}
