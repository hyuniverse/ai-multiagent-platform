package com.infobank.multiagentplatform.orchestrator.service.postprocessor;

import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ResultPostProcessor {
    Flux<String> process(Mono<Map<String, TaskResult>> rawResults);
}
