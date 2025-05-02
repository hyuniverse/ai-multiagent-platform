package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.planner.PlanJsonParser;
import com.infobank.multiagentplatform.orchestrator.planner.PromptBuilder;
import com.infobank.multiagentplatform.orchestrator.planner.llm.OpenAIClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

/**
 * Task planning 비즈니스 로직
 */
public class TaskPlannerService {

    private final PromptBuilder promptBuilder;
    private final OpenAIClient llmClient;
    private final PlanJsonParser parser;

    public TaskPlannerService(PromptBuilder promptBuilder,
                              OpenAIClient llmClient,
                              PlanJsonParser parser) {
        this.promptBuilder = promptBuilder;
        this.llmClient = llmClient;
        this.parser = parser;
    }

    /**
     * 사용자 요청과 사용 가능한 에이전트 목록을 받아 ExecutionPlan을 생성
     */
    public ExecutionPlan plan(StandardRequest request, List<AgentSummary> agents) {
        String prompt = promptBuilder.buildPrompt(request, agents);
        String rawJson = llmClient.callOpenAI(prompt);
        return parser.parse(rawJson);
    }
}
