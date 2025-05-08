package com.infobank.multiagentplatform.orchestrator.service;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.executor.ExecutionPlanExecutor;
import com.infobank.multiagentplatform.orchestrator.service.planner.TaskPlanner;
import com.infobank.multiagentplatform.orchestrator.service.postprocessor.ResultPostProcessor;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 전체 Orchestration 흐름을 담당하는 서비스
 * 1) 에이전트 목록 조회
 * 2) 실행 계획 생성
 * 3) 계획 실행
 * 4) 결과 후처리
 * 5) 응답 DTO 생성
 */
@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final BrokerClient brokerClient;
    private final TaskPlanner planner;
    private final ExecutionPlanExecutor executor;
    private final ResultPostProcessor postProcessor;

    public OrchestrationResponse orchestrate(OrchestrationServiceRequest request) {

        // 1. 사용 가능한 에이전트 요약 DTO 조회
        List<AgentSummaryResponse> agents = brokerClient.getAgentSummaries();

        // 2. 실행 계획 생성 (planner.plan 시, AgentSummaryResponse 리스트를 받도록 시그니처 변경 필요)
        ExecutionPlan plan = planner.plan(request, agents);

        // 3. 실행 계획 수행
        Map<String, TaskResult> rawResult = executor.executePlan(plan);

        return postProcessor.process(rawResult);
    }
}
