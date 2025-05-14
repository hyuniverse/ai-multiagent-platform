package com.infobank.multiagentplatform.orchestrator.service.postprocessor;

import com.infobank.multiagentplatform.orchestrator.llm.LLMClient;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LlmNarrativePostProcessor implements ResultPostProcessor {
    private final LLMClient llm;

    @Override
    public Mono<OrchestrationResponse> process(Mono<Map<String, TaskResult>> resultsMono) {
        return resultsMono
                // 1) Map<String,TaskResult>에서 직렬화된 문자열 생성
                .map(results ->
                        results.entrySet().stream()
                                .map(e -> e.getKey() + ": " + e.getValue().getRawResult())
                                .collect(Collectors.joining("\n"))
                )
                // 2) LLM 호출까지 논블로킹으로 연결
                .flatMap(payload -> {
                    String prompt = """
                    아래는 여러 에이전트가 생성한 결과물입니다.
                    사용자의 질의에 대한 답변 글을 하나의 완결된 설명문으로 재구성해 주세요:

                    """ + payload;
                    // llm.generateText가 블로킹 메서드라면 fromSupplier로 감싸기
                    return llm.generateText(Mono.just(prompt))
                            .map(OrchestrationResponse::of);
                });
    }
}
