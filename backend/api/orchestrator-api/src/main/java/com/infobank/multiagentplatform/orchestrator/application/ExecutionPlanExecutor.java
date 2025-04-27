package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.domain.TaskBlock;
import com.infobank.multiagentplatform.orchestrator.util.AgentCallTaskMapper;
import com.infobank.multiagentplatform.invoker.domain.AgentCallTask;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.infrastructure.rest.RestAgentInvoker;
import com.infobank.multiagentplatform.resilience.fallback.FallbackInvokerDecorator;
import com.infobank.multiagentplatform.resilience.logging.ExecutionContext;
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

    public void execute(ExecutionPlan plan, ExecutionContext context) {
        context.log("▶ Execution started");

        for (TaskBlock block : plan.getBlocks()) {
            for (AgentTask task : block.getTasks()) {
                try {
                    AgentMetadata metadata = agentMap.get(task.getAgentId());
                    if (metadata == null) {
                        context.log("❌ Agent not found for id: " + task.getAgentId());
                        continue;
                    }

                    AgentCallTask callTask = AgentCallTaskMapper.from(task, metadata, task.getGoal());

                    AgentInvoker invoker = new FallbackInvokerDecorator(
                            new RestAgentInvoker(), // 향후 invokerFactory 적용 가능
                            List.of() // fallback들 추가 가능
                    );

                    invoker.invoke(callTask, context);

                } catch (Exception e) {
                    context.log("💥 Exception during task execution: " + e.getMessage());
                }
            }
        }

        context.log("✅ Execution finished");
    }
}
