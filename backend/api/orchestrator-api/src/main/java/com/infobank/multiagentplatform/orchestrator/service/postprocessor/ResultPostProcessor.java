package com.infobank.multiagentplatform.orchestrator.service.postprocessor;

import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;

import java.util.Map;

public interface ResultPostProcessor {
    OrchestrationResponse process(Map<String, TaskResult> rawResults);
}
