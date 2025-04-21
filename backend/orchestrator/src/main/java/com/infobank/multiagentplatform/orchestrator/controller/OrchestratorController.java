package com.infobank.multiagentplatform.orchestrator.controller;

import com.infobank.multiagentplatform.orchestrator.application.TaskPlannerService;
import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.dto.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.dto.ExecutionPlanResponse;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.client.BrokerClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class OrchestratorController {

    private final TaskPlannerService taskPlannerService;
    private final BrokerClient brokerClient;

    @PostConstruct
    public void init() {
        System.out.println("🔥 OrchestratorController loaded!");
    }
    @PostMapping
    public ExecutionPlanResponse ask(@RequestBody StandardRequest request) {
        // 1. 현재 사용 가능한 Agent 목록 가져오기
        List<AgentSummary> availableAgents = brokerClient.getAgentSummaries();

        // 2. 실행 계획 수립
        ExecutionPlan plan = taskPlannerService.plan(request, availableAgents);

        // 3. 응답으로 변환
        return ExecutionPlanResponse.builder()
                .tasks(plan.getBlocks().stream()
                        .flatMap(block -> block.getTasks().stream())
                        .toList())
                .unassigned(plan.getUnassigned())
                .build();
    }
}
