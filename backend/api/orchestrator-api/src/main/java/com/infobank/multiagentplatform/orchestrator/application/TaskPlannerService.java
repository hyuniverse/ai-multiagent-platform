package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.planner.LLMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskPlannerService {

    private final LLMClient llmClient;

    /**
     * 사용자 요청 + 에이전트 목록을 바탕으로 실행 계획을 수립한다.
     */

    public ExecutionPlan plan(StandardRequest request, List<AgentSummary> agentSummaries) {
        ExecutionPlan plan = llmClient.plan(request, agentSummaries);
        return plan;
    }
}
