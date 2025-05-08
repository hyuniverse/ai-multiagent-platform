package com.infobank.multiagentplatform.orchestrator.model;


import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TaskResult {
    private final String taskId;
    private final String rawResult;
    private final Object parsedResult;


    @Builder
    public static TaskResult of(String taskId,  String rawResult, Object parsedResult) {
        return TaskResult.builder()
                .taskId(taskId)
                .rawResult(rawResult)
                .parsedResult(parsedResult)
                .build();
    }

}