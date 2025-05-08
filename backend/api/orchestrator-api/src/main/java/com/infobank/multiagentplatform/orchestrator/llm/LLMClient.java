package com.infobank.multiagentplatform.orchestrator.llm;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;

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
    ExecutionPlan plan(OrchestrationServiceRequest request, List<AgentSummaryResponse> agentSummaries);

    /**
     * 내러티브 생성: 직렬화된 태스크 결과를 받아
     * 한 편의 자연어 응답으로 재구성
     */
    String generateText(String prompt);
}
