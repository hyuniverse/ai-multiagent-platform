package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.planner.llm.LLMClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskPlannerServiceTest {

    private LLMClient llmClient;
    private TaskPlannerService taskPlannerService;

    @BeforeEach
    void setUp() {
        llmClient = mock(LLMClient.class);
        taskPlannerService = new TaskPlannerService(llmClient);
    }

    @Test
    @DisplayName("정상적인 plan 요청 시 LLMClient를 통해 ExecutionPlan을 반환한다")
    void plan_shouldReturnExecutionPlan() {
        // given
        StandardRequest request = StandardRequest.builder()
                .rawText("Summarize this report")
                .inputType("text")
                .build();
        AgentSummary agent = AgentSummary.builder()
                .agentId("agent-1")
                .description("summary agent")
                .inputType("text")
                .outputType("summary")
                .build();

        ExecutionPlan mockPlan = new ExecutionPlan(List.of(), List.of());

        when(llmClient.plan(request, List.of(agent))).thenReturn(mockPlan);

        // when
        ExecutionPlan result = taskPlannerService.plan(request, List.of(agent));

        // then
        assertThat(result).isEqualTo(mockPlan);
        verify(llmClient, times(1)).plan(request, List.of(agent));
    }

    @Test
    @DisplayName("LLMClient에서 예외 발생 시 서비스도 예외를 그대로 전파한다")
    void plan_whenLLMClientFails_shouldThrowException() {
        // given
        StandardRequest request = StandardRequest.builder()
                .rawText("Invalid")
                .inputType("text")
                .build();

        RuntimeException llmException = new RuntimeException("LLM failure");

        when(llmClient.plan(any(), any())).thenThrow(llmException);

        // then
        assertThatThrownBy(() -> taskPlannerService.plan(request, List.of()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("LLM failure");

        verify(llmClient).plan(any(), any());
    }
}
