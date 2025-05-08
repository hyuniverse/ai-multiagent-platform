package com.infobank.multiagentplatform.orchestrator.service.planner.llm;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;

import java.util.List;

/**
 * 자연어 요청과 가능한 에이전트 요약 DTO를 기반으로 실행 계획을 수립하는 클라이언트
 */
public interface LLMClient {

    /**
     * 플래너: 요청 → ExecutionPlan
     *
     * @param request 사용자 요청 DTO
     * @param agentSummaries 에이전트 요약 DTO 목록
     * @return 수립된 실행 계획
     */
    ExecutionPlan plan(OrchestrationRequest request, List<AgentSummaryResponse> agentSummaries);
}
