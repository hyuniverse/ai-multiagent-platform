package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.planner.llm.LLMClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Task planning 비즈니스 로직
 */
@Service
public class TaskPlannerService {

    private final LLMClient llmClient;

    public TaskPlannerService(@Qualifier("openAIClient") LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    public ExecutionPlan plan(StandardRequest request, List<AgentSummary> agents) {
        return llmClient.plan(request, agents);
    }
}
