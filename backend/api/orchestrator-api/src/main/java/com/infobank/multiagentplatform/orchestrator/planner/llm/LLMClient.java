package com.infobank.multiagentplatform.orchestrator.planner.llm;

import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;

import java.util.List;

public interface LLMClient {
    /**
     * 사용자 요청과 사용 가능한 에이전트 목록을 기반으로
     * 실행 계획(ExecutionPlan)을 수립한다.
     *
     * @param request 사용자 자연어 입력
     * @param agentSummaries 사용 가능한 에이전트 요약 리스트
     * @return 수립된 ExecutionPlan
     */
    ExecutionPlan plan(StandardRequest request, List<AgentSummary> agentSummaries);
}
