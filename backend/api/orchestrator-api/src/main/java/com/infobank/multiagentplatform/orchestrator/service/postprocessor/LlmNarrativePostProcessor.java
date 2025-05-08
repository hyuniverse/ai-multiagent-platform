package com.infobank.multiagentplatform.orchestrator.service.postprocessor;

import com.infobank.multiagentplatform.orchestrator.llm.LLMClient;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LlmNarrativePostProcessor implements ResultPostProcessor {
    private final LLMClient llm;

    @Override
    public OrchestrationResponse process(Map<String, TaskResult> results) {
        String payload = serializeResults(results);
        String prompt  = """
            아래는 여러 에이전트가 생성한 결과물입니다.
            사용자의 질의에 대한 답변 글을 하나의 완결된 설명문으로 재구성해 주세요:

            """ + payload;
        String narrative = llm.generateText(prompt);
        // ExecutionResult 내부에는 이제 narrative 텍스트만 담도록 설계
        return OrchestrationResponse.of(narrative);
    }

    private String serializeResults(Map<String, TaskResult> r) {
        return r.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue().getRawResult())
                .collect(Collectors.joining("\n"));
    }
}