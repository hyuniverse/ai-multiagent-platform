package com.infobank.multiagentplatform.orchestrator.controller;

import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.application.ExecutionPlanExecutor;
import com.infobank.multiagentplatform.orchestrator.application.TaskPlannerService;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.orchestrator.dto.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionResult;
import com.infobank.multiagentplatform.orchestrator.dto.ExecutionPlanResponse;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Orchestrator 엔드포인트 컨트롤러
 */
@RestController
@RequestMapping("/api/orchestrator")
@Validated
public class OrchestratorController {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorController.class);

    private final TaskPlannerService plannerService;
    private final ExecutionPlanExecutor executor;
    private final BrokerClient brokerClient;

    public OrchestratorController(TaskPlannerService plannerService,
                                  ExecutionPlanExecutor executor,
                                  BrokerClient brokerClient) {
        this.plannerService = plannerService;
        this.executor = executor;
        this.brokerClient = brokerClient;
    }

    /**
     * 사용자 요청을 받아 TaskPlannerService로 실행 계획을 생성하고,
     * ExecutionPlanExecutor로 실행한 결과를 함께 반환한다.
     */
    @PostMapping("/ask")
    public ResponseEntity<ExecutionPlanResponse> ask(@Valid @RequestBody StandardRequest request) {
        logger.info("/ask 요청 수신: {}", request);

        // 1. 현재 사용 가능한 에이전트 목록 조회
        List<AgentSummary> agents = brokerClient.getAgentSummaries();
        logger.debug("조회된 에이전트 수: {}", agents.size());

        // 2. 실행 계획 생성
        ExecutionPlan plan = plannerService.plan(request, agents);
        logger.info("생성된 ExecutionPlan 블록 수: {}", plan.getBlocks().size());

        // 3. 실행 계획 수행
        ExecutionResult execResult = executor.execute(plan);
        logger.info("실행된 태스크 결과 수: {}", execResult.getTaskResults().size());

        // 4. DTO로 매핑하여 반환
        ExecutionPlanResponse response = ExecutionPlanResponse.from(plan, execResult);
        return ResponseEntity.ok(response);
    }
}
