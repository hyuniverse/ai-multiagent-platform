package com.infobank.multiagentplatform.orchestrator.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionPlanTest {
    @Test
    @DisplayName("ExecutionPlan JSON 매핑 테스트 - 중첩 구조 정상 역직렬화")
    void shouldDeserializeExecutionPlanWithMultipleBlocksAndTasks() throws Exception {
        // given
        String json = """
        {
          "blocks": [
            {
              "tasks": [
                {
                  "goal": "요약",
                  "agentId": "agent-1",
                  "inputType": "text",
                  "inputFrom": null
                },
                {
                  "goal": "감정 분석",
                  "agentId": "agent-2",
                  "inputType": "text",
                  "inputFrom": null
                }
              ]
            },
            {
              "tasks": [
                {
                  "goal": "번역",
                  "agentId": "agent-3",
                  "inputType": "text",
                  "inputFrom": "agent-1"
                }
              ]
            }
          ],
          "unassigned": [
            {
              "goal": "요약 불가",
              "agentId": null,
              "inputType": "text",
              "inputFrom": null
            }
          ]
        }
        """;

        ObjectMapper mapper = new ObjectMapper();

        // when
        ExecutionPlan plan = mapper.readValue(json, ExecutionPlan.class);

        // then
        assertThat(plan).isNotNull();
        assertThat(plan.getBlocks()).hasSize(2);

        // 첫 번째 블록에 task 2개
        TaskBlock block1 = plan.getBlocks().getFirst();
        assertThat(block1.getTasks()).hasSize(2);
        assertThat(block1.getTasks().get(0).getGoal()).isEqualTo("요약");
        assertThat(block1.getTasks().get(1).getAgentId()).isEqualTo("agent-2");

        // 두 번째 블록에 task 1개
        TaskBlock block2 = plan.getBlocks().get(1);
        assertThat(block2.getTasks()).hasSize(1);
        assertThat(block2.getTasks().getFirst().getInputFrom()).isEqualTo("agent-1");

        // unassigned task 확인
        assertThat(plan.getUnassigned()).hasSize(1);
        AgentTask unassigned = plan.getUnassigned().getFirst();
        assertThat(unassigned.getGoal()).isEqualTo("요약 불가");
        assertThat(unassigned.getAgentId()).isNull();
    }


}