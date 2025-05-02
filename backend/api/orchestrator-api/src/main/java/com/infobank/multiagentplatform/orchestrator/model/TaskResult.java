package com.infobank.multiagentplatform.orchestrator.model;

public class TaskResult {
    private final String taskId;
    private final Object result;

    public TaskResult(String taskId, Object result) {
        this.taskId = taskId;
        this.result = result;
    }

    public String getTaskId() {
        return taskId;
    }

    public Object getResult() {
        return result;
    }
}
