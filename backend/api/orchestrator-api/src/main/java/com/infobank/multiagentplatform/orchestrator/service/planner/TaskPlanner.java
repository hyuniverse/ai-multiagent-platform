package com.infobank.multiagentplatform.orchestrator.service.planner;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.llm.LLMClient;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Task planning 비즈니스 로직
 */
@Service
@Slf4j
public class TaskPlanner {

    private final LLMClient llmClient;
    private final ReactiveMetricOperator metricOperator;

    public TaskPlanner(@Qualifier("openAIClient") LLMClient llmClient, ReactiveMetricOperator metricOperator) {
        this.llmClient = llmClient;
        this.metricOperator = metricOperator;
    }

    /**
     * ExecutionPlan 생성
     *
     * @param request 사용자 요청 DTO
     * @param agents  사용 가능한 에이전트 요약 DTO 목록
     * @return 실행 계획
     */
    public Mono<ExecutionPlan> plan(OrchestrationServiceRequest request, Mono<List<AgentSummaryResponse>> agents) {
        
        return agents
                .flatMap(agentList -> {
                    return llmClient.plan(request, Mono.just(agentList));
                })
                .doOnError(error -> log.error("실행 계획 생성 실패: {}", error.getMessage(), error))
                .transform(metricOperator.measure("orchestration.planner"))
                .onErrorResume(e -> {
                    return Mono.error(new IllegalStateException("Plan generation failed", e));
                });
    }
}
