package com.infobank.multiagentplatform.orchestrator.service.planner;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;
import com.infobank.multiagentplatform.orchestrator.llm.LLMClient;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Task planning 비즈니스 로직
 */
@Service
public class TaskPlanner {

    private final LLMClient llmClient;

    public TaskPlanner(@Qualifier("openAIClient") LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * ExecutionPlan 생성
     *
     * @param request 사용자 요청 DTO
     * @param agents  사용 가능한 에이전트 요약 DTO 목록
     * @return 실행 계획
     */
    public Mono<ExecutionPlan> plan(OrchestrationServiceRequest request, Mono<List<AgentSummaryResponse>> agents) {
        return llmClient.plan(request, agents);
    }
}
