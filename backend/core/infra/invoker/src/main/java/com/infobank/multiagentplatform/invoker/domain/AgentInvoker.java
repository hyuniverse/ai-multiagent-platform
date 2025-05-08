package com.infobank.multiagentplatform.invoker.domain;


import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;

public interface AgentInvoker {
    AgentInvocationResponse invoke(AgentInvocationRequest task);
}
