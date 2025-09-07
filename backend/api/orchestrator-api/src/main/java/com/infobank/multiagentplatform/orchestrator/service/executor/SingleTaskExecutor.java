package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import com.infobank.multiagentplatform.orchestrator.exception.AgentInactiveException;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;

@Component
@RequiredArgsConstructor
public class SingleTaskExecutor {
    private final AgentInvokerFactory invokerFactory;
    private final ReactiveMetricOperator metricOperator;

    @Value("${orchestrator.retries.parse.max-attempts:3}")
    private int parseRetryMaxAttempts;

    @Value("${orchestrator.retries.parse.backoff:1s}")
    private Duration parseRetryBackoff;

    public Mono<TaskResult> executeReactive(
            AgentTask task,
            Map<String, AgentDetailResponse> metadataMap,
            Map<String, TaskResult> results
    ) {
        AgentDetailResponse meta = metadataMap.get(task.getAgentId());
        if (meta == null || meta.getStatus() != AgentStatus.ACTIVE) {
            return Mono.error(new AgentInactiveException(task.getAgentId()));
        }
        Object rawInput = (task.getInputFrom() == null)
                ? task.getInstruction()
                : results.get(task.getInputFrom()).getParsedResult();
        String payload = rawInput.toString();
        System.out.println("payload: " + payload);
        AgentInvocationRequest request = AgentInvocationRequest.of(meta, payload);
        AgentInvoker invoker = invokerFactory.getInvoker(meta.getProtocol());

        return invoker.invoke(request)
                .transform(metricOperator.measure("orchestration.executor.task"))
                .map(response ->
                        TaskResult.of(
                                task.getId(),
                                response.getRawResponse(),
                                response.getParsedResult()
                        )
                ).retryWhen(Retry.backoff(parseRetryMaxAttempts, parseRetryBackoff)
                        .filter(err -> err instanceof PlanParsingException)
                )
                .onErrorResume(PlanParsingException.class, ex ->
                        Mono.just(TaskResult.of(
                                task.getId(),
                                null,
                                Map.of(
                                        "fallback", "parse_error",
                                        "reason", ex.getMessage()
                                )
                        ))
                );
    }
}
