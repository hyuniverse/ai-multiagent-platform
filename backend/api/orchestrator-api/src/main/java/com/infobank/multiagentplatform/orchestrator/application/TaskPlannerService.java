package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.orchestrator.dto.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.planner.OpenAIClientImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

/**
 * Task planning 비즈니스 로직
 */
public class TaskPlannerService {

    private final PromptContextBuilder promptBuilder;
    private final OpenAIClientImpl llmClient;
    private final ExecutionPlanParser parser;

    public TaskPlannerService(PromptContextBuilder promptBuilder,
                              OpenAIClientImpl llmClient,
                              ExecutionPlanParser parser) {
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
