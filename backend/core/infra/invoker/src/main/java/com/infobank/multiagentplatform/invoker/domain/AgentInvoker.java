package com.infobank.multiagentplatform.invoker.domain;

import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentInvocationResponse;
import reactor.core.publisher.Mono;

public interface AgentInvoker {
    Mono<AgentInvocationResponse> invoke(AgentInvocationRequest task);
}
