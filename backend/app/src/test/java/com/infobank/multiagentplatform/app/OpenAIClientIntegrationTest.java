package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.planner.llm.OpenAIClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OpenAIClientIntegrationTest {

    @Autowired
    private OpenAIClient openAIClient;

    @Test
    @DisplayName("실제 OpenAI API를 호출하여 ExecutionPlan을 정상 수립할 수 있다")
    void should_call_openai_and_build_execution_plan_successfully() {
        // given
        StandardRequest request = StandardRequest.builder()
                .rawText("텍스트 요약하고 한국어로 번역해줘")
                .inputType("text")
                .build();

        AgentSummary summarizer = AgentSummary.builder()
                .agentId("agent-summarizer")
                .description("텍스트 요약을 수행합니다.")
                .inputType("text")
                .outputType("summary")
                .build();

        AgentSummary translator = AgentSummary.builder()
                .agentId("agent-translator")
                .description("영어 텍스트를 한국어로 번역합니다.")
                .inputType("text")
                .outputType("ko-text")
                .build();

        List<AgentSummary> agentList = List.of(summarizer, translator);

        // when
        ExecutionPlan plan = openAIClient.plan(request, agentList);

        // then
        assertThat(plan).isNotNull();
        assertThat(plan.getBlocks()).isNotEmpty();

        System.out.println("💡 ExecutionPlan 결과:");
        plan.getBlocks().forEach(System.out::println);
    }
}
