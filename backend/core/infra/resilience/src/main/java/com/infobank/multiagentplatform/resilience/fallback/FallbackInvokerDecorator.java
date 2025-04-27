package com.infobank.multiagentplatform.resilience.fallback;

import com.infobank.multiagentplatform.invoker.domain.AgentCallTask;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.domain.AgentRequest;
import com.infobank.multiagentplatform.invoker.domain.AgentResult;
import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.resilience.logging.ExecutionContext;

import java.util.List;

public class FallbackInvokerDecorator implements AgentInvoker {

    private final AgentInvoker delegate;
    private final List<AgentInvoker> fallbackInvokers;

    public FallbackInvokerDecorator(AgentInvoker delegate, List<AgentInvoker> fallbackInvokers) {
        this.delegate = delegate;
        this.fallbackInvokers = fallbackInvokers;
    }

    @Override
    public AgentResult invoke(AgentCallTask task, ExecutionContext context) {
        try {
            context.log("Invoking primary agent: " + task.getAgentId());
            AgentResult result = delegate.invoke(task, context);
            context.recordCall(task.getAgentId(), true, "Primary succeeded");
            return result;
        } catch (Exception e) {
            context.recordCall(task.getAgentId(), false, "Primary failed: " + e.getMessage());

            for (AgentInvoker fallback : fallbackInvokers) {
                try {
                    context.log("Trying fallback for agent: " + task.getAgentId());
                    AgentResult result = fallback.invoke(task, context);
                    context.recordCall(task.getAgentId(), true, "Fallback succeeded");
                    return result;
                } catch (Exception fe) {
                    context.recordCall(task.getAgentId(), false, "Fallback failed: " + fe.getMessage());
                }
            }

            throw new RuntimeException("All fallback attempts failed for agent: " + task.getAgentId());
        }
    }
}
