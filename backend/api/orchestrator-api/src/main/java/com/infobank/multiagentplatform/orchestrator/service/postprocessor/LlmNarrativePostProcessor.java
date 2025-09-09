package com.infobank.multiagentplatform.orchestrator.service.postprocessor;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;
import com.infobank.multiagentplatform.orchestrator.llm.LLMClient;
import com.infobank.multiagentplatform.orchestrator.model.result.TaskResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LlmNarrativePostProcessor implements ResultPostProcessor {
    private final LLMClient llm;
    private final ReactiveMetricOperator metricOperator; // TODO: 향후 Flux 계측 확장 시 사용
    private final @Qualifier("boundedElasticScheduler") Scheduler boundedElasticScheduler;

    @Override
    public Flux<String> process(Mono<Map<String, TaskResult>> resultsMono) {
        return resultsMono
                .map(results -> results.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue().getRawResult())
                        .collect(Collectors.joining("\n"))
                )
                .flatMapMany(payload -> {
                    String prompt = """
                    아래는 여러 에이전트가 생성한 결과물입니다.
                    사용자의 질의에 대한 답변 글을 하나의 완결된 설명문으로 재구성해 주세요:

                    """ + payload;
                    return llm.streamCompletion(Mono.just(prompt))
                            .subscribeOn(boundedElasticScheduler);
                });
    }
}
