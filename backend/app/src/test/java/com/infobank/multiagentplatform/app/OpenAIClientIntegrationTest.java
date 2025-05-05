package com.infobank.multiagentplatform.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.config.LLMClientProperties;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.planner.PlanJsonParser;
import com.infobank.multiagentplatform.orchestrator.planner.PromptBuilder;
import com.infobank.multiagentplatform.orchestrator.planner.llm.OpenAIClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                OpenAIClient.class,
                OpenAIClientIntegrationTest.TestConfig.class
        }
)
@Import(OpenAIClientIntegrationTest.TestConfig.class)
public class OpenAIClientIntegrationTest {

    @Autowired
    private OpenAIClient openAIClient;

    @Test
    @DisplayName("실제 OpenAI API를 호출하여 ExecutionPlan을 정상 수립할 수 있다")
    void should_call_openai_and_build_execution_plan_successfully() {
        // given
        StandardRequest request = StandardRequest.builder()
                .rawText("이 뉴스 기사에 대해 요약하고, 감정 분석, 주제 분류를 해줘.")
                .inputType("text")
                .build();

        AgentSummary keywordExtractor = AgentSummary.builder()
                .agentId("agent-keyword-extractor")
                .description("중요 키워드를 추출합니다.")
                .inputType("text")
                .outputType("keyword-list")
                .build();

        AgentSummary imageTagGenerator = AgentSummary.builder()
                .agentId("agent-tag-generator")
                .description("이미지 검색을 위한 태그를 생성합니다.")
                .inputType("keyword-list")
                .outputType("image-tags")
                .build();

        AgentSummary dateExtractor = AgentSummary.builder()
                .agentId("agent-date-extractor")
                .description("텍스트에서 날짜 및 시간을 추출합니다.")
                .inputType("text")
                .outputType("datetime-list")
                .build();

        AgentSummary scheduleSummarizer = AgentSummary.builder()
                .agentId("agent-schedule-summarizer")
                .description("날짜 정보를 기반으로 일정을 요약합니다.")
                .inputType("datetime-list")
                .outputType("schedule-summary")
                .build();


        AgentSummary sentimentAnalyzer = AgentSummary.builder()
                .agentId("agent-sentiment")
                .description("텍스트의 감정(긍정/부정/중립)을 분석합니다.")
                .inputType("summary")
                .outputType("sentiment")
                .build();


        AgentSummary summarizer = AgentSummary.builder()
                .agentId("agent-summarizer")
                .description("텍스트 요약을 수행합니다.")
                .inputType("text")
                .outputType("text")
                .build();

        AgentSummary translator = AgentSummary.builder()
                .agentId("agent-translator")
                .description("영어 텍스트를 한국어로 번역합니다.")
                .inputType("text")
                .outputType("ko-text")
                .build();


        AgentSummary classifier = AgentSummary.builder()
                .agentId("agent-topic-classifier")
                .description("텍스트의 주제를 분류합니다.")
                .inputType("text")
                .outputType("topic-label")
                .build();

        List<AgentSummary> agentList = List.of(summarizer, translator,sentimentAnalyzer,dateExtractor,scheduleSummarizer,keywordExtractor,imageTagGenerator,classifier);

        // when
        ExecutionPlan plan = openAIClient.plan(request, agentList);

        // then
        assertThat(plan).isNotNull();
        assertThat(plan.getBlocks()).isNotEmpty();

        System.out.println("💡 ExecutionPlan 결과:");
        plan.getBlocks().forEach(System.out::println);
    }

    /**
     * Test 전용 최소 Bean 등록.
     * broker 등 불필요한 의존을 전부 제거하고
     * OpenAIClient만 실행 가능한 상태로 만들어 줍니다.
     */
    static class TestConfig {
        @Bean
        public LLMClientProperties llmClientProperties() {
            LLMClientProperties props = new LLMClientProperties();
            props.setApiKey(System.getenv("OPENAI_API_KEY"));          // 환경변수에 API 키를 꺼내거나 직접 넣으세요
            props.setApiUrl("https://api.openai.com/v1/chat/completions");
            props.setModel("gpt-4-0125-preview");
            return props;
        }

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder();
        }

        @Bean
        public PromptBuilder promptBuilder() throws Exception {
            // 실제 템플릿을 쓰고 싶다면 클래스패스에 prompt-templates/default-prompt.json이 있어야 합니다.
            return new PromptBuilder(
                    new ObjectMapper(),
                    new ClassPathResource("prompt-templates/default-prompt.json")
            );
        }

        @Bean
        public PlanJsonParser planJsonParser() {
            return new PlanJsonParser(new ObjectMapper());
        }
    }
}
