package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.postprocessor.ResultPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionPlanExecutor {
    private final BrokerClient brokerClient;
    private final TaskBlockExecutor blockExecutor;
    private final ResultPostProcessor postProcessor;

    public Map<String, TaskResult> executePlan(ExecutionPlan plan) {
        // 1. 모든 에이전트 ID 수집
        List<String> agentIds = plan.getBlocks().stream()
                .flatMap(b -> b.getTasks().stream().map(AgentTask::getAgentId))
                .distinct()
                .collect(Collectors.toList());

        // 2. batch 조회
        List<AgentDetailResponse> details = brokerClient.getAgentMetadataBatch(agentIds);
        Map<String, AgentDetailResponse> metadataMap = details.stream()
                .collect(Collectors.toMap(AgentDetailResponse::getUuid, Function.identity()));

        // 3. 블록별 실행
        Map<String, TaskResult> results = new ConcurrentHashMap<>();
        for (var block : plan.getBlocks()) {
            blockExecutor.executeBlock(block, metadataMap, results);
        }

        return results;
    }
}