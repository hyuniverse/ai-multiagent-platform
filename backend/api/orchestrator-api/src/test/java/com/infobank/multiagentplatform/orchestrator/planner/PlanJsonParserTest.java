package com.infobank.multiagentplatform.orchestrator.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.EMPTY_PLAN_EXCEPTION;
import static com.infobank.multiagentplatform.orchestrator.messages.ExceptionMessages.JSON_PARSE_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanJsonParserTest {

    private PlanJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new PlanJsonParser(new ObjectMapper());
    }

    @Test
    @DisplayName("정상적인 JSON을 ExecutionPlan으로 변환한다")
    void parse_validJson_shouldReturnExecutionPlan() {
        String json = """
            {
              "blocks": [
                {
                  "tasks": [
                    {
                      "goal": "Summarize text",
                      "agentId": "agent-1",
                      "inputType": "text",
                      "inputFrom": null
                    }
                  ]
                }
              ],
              "unassigned": []
            }
            """;

        ExecutionPlan plan = parser.parse(json);

        assertThat(plan).isNotNull();
        assertThat(plan.getBlocks()).hasSize(1);
        assertThat(plan.getBlocks().getFirst().getTasks()).hasSize(1);
        assertThat(plan.getUnassigned()).isEmpty();
    }

    @Test
    @DisplayName("잘못된 JSON을 입력하면 PlanParsingException이 발생한다")
    void parse_invalidJson_shouldThrowException() {
        String malformedJson = "{ blocks: [ { invalid } ]"; // 잘못된 JSON

        assertThatThrownBy(() -> parser.parse(malformedJson))
                .isInstanceOf(PlanParsingException.class)
                .hasMessageContaining(JSON_PARSE_EXCEPTION);
    }

    @Test
    @DisplayName("블록이 비어있는 JSON을 입력하면 PlanParsingException이 발생한다")
    void parse_emptyBlocks_shouldThrowException() {
        String emptyBlocksJson = """
            {
              "blocks": [],
              "unassigned": []
            }
            """;

        assertThatThrownBy(() -> parser.parse(emptyBlocksJson))
                .isInstanceOf(PlanParsingException.class)
                .hasMessageContaining(EMPTY_PLAN_EXCEPTION);
    }

    @Test
    @DisplayName("여러 블록과 다수의 태스크를 포함한 복잡한 JSON도 정상 파싱된다")
    void parse_complexJson_shouldReturnExecutionPlan() {
        String complexJson = """
        {
          "blocks": [
            {
              "tasks": [
                {
                  "goal": "분석 결과 요약",
                  "agentId": "agent-summary",
                  "inputType": "text",
                  "inputFrom": null
                },
                {
                  "goal": "감정 분석",
                  "agentId": "agent-sentiment",
                  "inputType": "text",
                  "inputFrom": null
                }
              ]
            },
            {
              "tasks": [
                {
                  "goal": "보고서 생성",
                  "agentId": "agent-writer",
                  "inputType": "text",
                  "inputFrom": "agent-summary"
                }
              ]
            }
          ],
          "unassigned": [
            {
              "goal": "이미지 분석",
              "agentId": null,
              "inputType": "image",
              "inputFrom": null
            }
          ]
        }
        """;

        ExecutionPlan plan = parser.parse(complexJson);

        assertThat(plan.getBlocks()).hasSize(2);
        assertThat(plan.getBlocks().get(0).getTasks()).hasSize(2);
        assertThat(plan.getBlocks().get(1).getTasks()).hasSize(1);

        assertThat(plan.getUnassigned()).hasSize(1);
        assertThat(plan.getUnassigned().getFirst().getAgentId()).isNull();
        assertThat(plan.getUnassigned().getFirst().getInputType()).isEqualTo("image");
    }
}
