package com.infobank.multiagentplatform.resilience.logging;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;

public class ExecutionContext {

    private final String requestId;
    private final Instant startTime;
    private ExecutionPlan executionPlan;
    private final List<String> executionLogs = new ArrayList<>();
    private final List<AgentCallRecord> callRecords = new ArrayList<>();

    public ExecutionContext(String requestId) {
        this.requestId = requestId;
        this.startTime = Instant.now();
    }

    public void setExecutionPlan(ExecutionPlan plan) {
        this.executionPlan = plan;
        log("Execution plan set.");
    }

    public void log(String message) {
        executionLogs.add("[%s] %s".formatted(Instant.now(), message));
    }

    public void recordCall(String agentId, boolean success, String message) {
        callRecords.add(new AgentCallRecord(agentId, success, message, Instant.now()));
    }

    public String getRequestId() {
        return requestId;
    }

    public ExecutionPlan getExecutionPlan() {
        return executionPlan;
    }

    public List<String> getLogs() {
        return executionLogs;
    }

    public List<AgentCallRecord> getCallRecords() {
        return callRecords;
    }

    public Instant getStartTime() {
        return startTime;
    }
}
