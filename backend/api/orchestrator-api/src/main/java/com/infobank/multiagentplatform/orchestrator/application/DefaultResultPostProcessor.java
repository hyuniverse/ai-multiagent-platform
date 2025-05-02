package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.model.ExecutionResult;
import com.infobank.multiagentplatform.orchestrator.model.TaskResult;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;

@Service
public class DefaultResultPostProcessor implements ResultPostProcessor {
    @Override
    public ExecutionResult process(Map<String,AgentResult> rawResults) {
        List<TaskResult> list = rawResults.entrySet().stream()
                .map(e -> new TaskResult(e.getKey(), e.getValue().getParsedResult()))
                .collect(Collectors.toList());
        return new ExecutionResult(list, rawResults);
    }
}