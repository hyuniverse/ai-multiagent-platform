package com.infobank.multiagentplatform.orchestrator.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.config.LLMClientProperties;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.infobank.multiagentplatform.orchestrator.model.plan.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.service.planner.PlanJsonParser;
import com.infobank.multiagentplatform.orchestrator.service.planner.PromptBuilder;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OpenAI API 호출 및 ExecutionPlan 수립 구현체
 */
@Component
@Qualifier("openAIClient")
public class OpenAIClient implements LLMClient {

    private final WebClient webClient;
    private final String model;
    private final PromptBuilder promptBuilder;
    private final PlanJsonParser planJsonParser;

    public OpenAIClient(WebClient.Builder webClientBuilder,
                        LLMClientProperties props,
                        PromptBuilder promptBuilder,
                        PlanJsonParser planJsonParser) {

        this.webClient = webClientBuilder
                .baseUrl(props.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
                .build();

        this.model           = props.getModel();
        this.promptBuilder   = promptBuilder;
        this.planJsonParser  = planJsonParser;
    }


    @Override
    @Retry(name = "openAIClientRetry")
    @CircuitBreaker(name = "openAIClientCB", fallbackMethod = "planFallback")
    // Plan 생성
    public Mono<ExecutionPlan> plan(OrchestrationServiceRequest request, Mono<List<AgentSummaryResponse>> agentSummaries) {
        Mono<String> prompt = promptBuilder.buildPrompt(request, agentSummaries);
        return callOpenAI(prompt)
                .flatMap(response -> {
                    try {
                        return Mono.just(planJsonParser.parse(response));
                    } catch (PlanParsingException e) {
                        return Mono.error(e); // 명시적으로 예외로 감싸기
                    }
                });
    }

    private Mono<ExecutionPlan> planFallback(OrchestrationServiceRequest request,
                                             Mono<List<AgentSummaryResponse>> agentSummaries,
                                             Throwable ex) {
        return Mono.error(new IllegalStateException("실행 계획 수립 실패: " + ex.getMessage(), ex));
    }

    @Override
    @Retry(name = "openAIClientRetry")
    @CircuitBreaker(name = "openAIClientCB", fallbackMethod = "textFallback")
    // ping 테스트 용
    public Mono<String> generateText(Mono<String> prompt) {
        return callOpenAI(prompt);
    }

    private Mono<String> textFallback(Mono<String> prompt, Throwable ex) {
        return Mono.just("죄송합니다. 현재 텍스트 생성이 불가합니다.");
    }

    /**
     * OpenAI에 프롬프트를 보내고 응답 텍스트 추출
     */
    private Mono<String> callOpenAI(Mono<String> promptMono) {
        return promptMono.flatMap(prompt -> {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            return webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            c -> c.createException().flatMap(Mono::error))
                    .bodyToMono(JsonNode.class)
                    .handle((resp, sink) -> {
                        JsonNode choices = Optional.ofNullable(resp)
                                .map(r -> r.path("choices"))
                                .orElseThrow(() -> new IllegalStateException("OpenAI 응답이 null입니다."));
                        if (!choices.isArray() || choices.isEmpty()) {
                            sink.error(new IllegalStateException("No choices in OpenAI response"));
                            return;
                        }
                        sink.next(choices.get(0).path("message").path("content").asText());
                    });
        });
    }

}
