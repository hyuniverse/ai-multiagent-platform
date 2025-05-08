package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SingleTaskExecutor {
    private final AgentInvokerFactory invokerFactory;

    public TaskResult execute(
            AgentTask task,
            Map<String, AgentDetailResponse> metadataMap,
            Map<String, TaskResult> results
    ) {
        AgentDetailResponse meta = metadataMap.get(task.getAgentId());
        Object rawInput = (task.getInputFrom()==null)
                ? task.getInstruction()
                : results.get(task.getInputFrom()).getParsedResult();
        String payload = rawInput.toString();

        AgentInvocationRequest request = AgentInvocationRequest.of(meta, payload);
        AgentInvoker invoker = invokerFactory.getInvoker(meta.getProtocol());
        AgentInvocationResponse response = invoker.invoke(request);

        return TaskResult.of(task.getId(), response.getRawResponse(), response.getParsedResult());
    }
}
