package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionResult;
import com.infobank.multiagentplatform.orchestrator.model.TaskResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultResultPostProcessorTest {

    private DefaultResultPostProcessor postProcessor;

    @BeforeEach
    void setUp() {
        postProcessor = new DefaultResultPostProcessor();
    }

    @Test
    @DisplayName("AgentResult를 ExecutionResult로 정상 변환한다")
    void process_validInput_shouldReturnExecutionResult() {
        AgentResult result1 = new AgentResult("raw1", "summary result");
        AgentResult result2 = new AgentResult("raw2", "sentiment result");

        Map<String, AgentResult> rawResults = Map.of(
                "task-1", result1,
                "task-2", result2
        );

        ExecutionResult execResult = postProcessor.process(rawResults);

        assertThat(execResult.getOrderedResults()).hasSize(2);
        assertThat(execResult.getRawResults()).isEqualTo(rawResults);

        TaskResult tr1 = execResult.getOrderedResults().getFirst();
        assertThat(tr1.getTaskId()).isIn("task-1", "task-2");
        assertThat(tr1.getResult()).isIn("summary result", "sentiment result");
    }

    @Test
    @DisplayName("parsedResult가 null이어도 NPE 없이 처리된다")
    void process_nullParsedResult_shouldHandleGracefully() {
        AgentResult result = new AgentResult("raw", null);
        Map<String, AgentResult> rawResults = Map.of("task-1", result);

        ExecutionResult execResult = postProcessor.process(rawResults);

        assertThat(execResult.getOrderedResults()).hasSize(1);
        assertThat(execResult.getOrderedResults().getFirst().getResult()).isNull();
    }

    @Test
    @DisplayName("빈 결과를 입력하면 빈 ExecutionResult가 반환된다")
    void process_emptyInput_shouldReturnEmptyResult() {
        ExecutionResult execResult = postProcessor.process(Map.of());

        assertThat(execResult.getOrderedResults()).isEmpty();
        assertThat(execResult.getRawResults()).isEmpty();
    }
}
