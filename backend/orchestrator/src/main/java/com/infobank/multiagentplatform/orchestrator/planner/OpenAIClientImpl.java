package com.infobank.multiagentplatform.orchestrator.planner;

import com.infobank.multiagentplatform.orchestrator.config.LLMClientProperties;
import com.infobank.multiagentplatform.orchestrator.domain.AgentTask;
import com.infobank.multiagentplatform.orchestrator.domain.TaskBlock;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.dto.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.parser.ExecutionPlanParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAIClientImpl implements LLMClient {

    private final RestTemplate restTemplate;
    private final LLMClientProperties properties;
    //private final ExecutionPlanParser executionPlanParser; // JSON → ExecutionPlan 변환 담당

    @Override
    public ExecutionPlan plan(StandardRequest request, List<AgentSummary> agentSummaries) {
        // 1. 프롬프트 생성
        String prompt = buildPrompt(request, agentSummaries);

        // 2. OpenAI API 호출
        String response = callOpenAI(prompt);

        // 3. 응답 파싱
        AgentTask task = AgentTask.builder()
                .goal("summarize")
                .agentId("summarizer-v1")
                .inputType("text")
                .build();
        return new ExecutionPlan(List.of(new TaskBlock(List.of(task))), List.of());

    }

    private String buildPrompt(StandardRequest request, List<AgentSummary> agentSummaries) {
        // TODO: PromptContextBuilder를 활용하여 실제 prompt 구성
        return "PromptContextBuilder 개발 후 적용 예정";
    }

    private String callOpenAI(String prompt) {
        // TODO: OpenAI API 호출 로직 구현
        // 현재는 목업 반환
        return "{ \"tasks\": [] }";
    }
}
