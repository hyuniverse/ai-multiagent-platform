package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.domain.TaskBlock;
import com.infobank.multiagentplatform.orchestrator.util.AgentCallTaskMapper;
import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.infrastructure.rest.RestAgentInvoker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionPlanExecutor {

    // 임시 agent 정보 map (agentId → metadata)
    private Map<String, AgentMetadata> agentMap;

    public void preloadAgents(List<AgentMetadata> agents) {
        this.agentMap = agents.stream().collect(Collectors.toMap(
                AgentMetadata::getId, a -> a
        ));
    }

    public void execute(ExecutionPlan plan) {
    }
}
