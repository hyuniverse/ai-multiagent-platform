package com.infobank.multiagentplatform.orchestrator.model.result;


import lombok.Builder;
import lombok.Getter;

@Getter
public class TaskResult {
    private final String taskId;
    private final String rawResult;
    private final Object parsedResult;

    @Builder
    private TaskResult(String taskId, String rawResult, Object parsedResult) {
        this.taskId = taskId;
        this.rawResult = rawResult;
        this.parsedResult = parsedResult;
    }

    public static TaskResult of(String taskId,  String rawResult, Object parsedResult) {
        return TaskResult.builder()
                .taskId(taskId)
                .rawResult(rawResult)
                .parsedResult(parsedResult)
                .build();
    }
}