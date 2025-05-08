package com.infobank.multiagentplatform.orchestrator.application.executor;

import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.orchestrator.util.AgentCallTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SingleTaskExecutor {
    private final BrokerClient brokerClient;
    private final AgentInvokerFactory invokerFactory;

    public AgentResult execute(AgentTask task, Map<String,AgentResult> results) {
        AgentMetadata meta = brokerClient.getAgentMetadata(task.getAgentId());
        Object input = task.getInputFrom() == null
                ? task.getGoal()
                : results.get(task.getInputFrom()).getParsedResult();
        String payload = Objects.toString(input, "");
        AgentCallTask call = AgentCallTaskMapper.from(task, meta, payload);
        AgentInvoker invoker = invokerFactory.getInvoker(meta.getProtocol().name().toLowerCase());
        Object raw = invoker.invoke(call);
        return new AgentResult(Objects.toString(raw, ""), raw);
    }
}
