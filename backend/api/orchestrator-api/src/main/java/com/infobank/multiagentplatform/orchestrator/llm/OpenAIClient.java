package com.infobank.multiagentplatform.orchestrator.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.infobank.multiagentplatform.commons.metrics.ReactiveMetricOperator;
import org.springframework.beans.factory.annotation.Value;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
    private final ObjectMapper objectMapper;
    private final Scheduler boundedElasticScheduler;
    private final ReactiveMetricOperator metricOperator;
    private final Duration operatorTimeout;

    private static final Logger log = LoggerFactory.getLogger(OpenAIClient.class);


    public OpenAIClient(@Qualifier("llmWebClientBuilder") WebClient.Builder webClientBuilder,
                        LLMClientProperties props,
                        PromptBuilder promptBuilder,
                        PlanJsonParser planJsonParser,
                        ObjectMapper objectMapper,
                        @Qualifier("boundedElasticScheduler") Scheduler boundedElasticScheduler,
                        ReactiveMetricOperator metricOperator,
                        @Value("${orchestrator.timeouts.llm-operator:500ms}") Duration operatorTimeout) {

        this.webClient = webClientBuilder
                .baseUrl(props.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
                .build();

        this.model           = props.getModel();
        this.promptBuilder   = promptBuilder;
        this.planJsonParser  = planJsonParser;
        this.objectMapper    = objectMapper;
        this.boundedElasticScheduler = boundedElasticScheduler;
        this.metricOperator  = metricOperator;
        this.operatorTimeout = operatorTimeout;
    }

    /**
     * ExecutionPlan 생성
     * @param request 사용자 요청 DTO
     * @param agentSummaries 에이전트 요약 DTO 목록
     * @return
     */
    @Override
    @Retry(name = "openaiRetry")
    @CircuitBreaker(name = "openaiCB", fallbackMethod = "planFallback")
    @RateLimiter(name = "openaiRL")
    public Mono<ExecutionPlan> plan(OrchestrationServiceRequest request, Mono<List<AgentSummaryResponse>> agentSummaries) {
        Mono<String> prompt = promptBuilder
                .buildPrompt(request, agentSummaries)
                .transform(metricOperator.measure("orchestration.planner.prompt"));

        Mono<String> httpMono = getCompletion(prompt);

        return httpMono
                .flatMap(response ->
                        Mono.fromCallable(() -> planJsonParser.parse(response))
                                .subscribeOn(boundedElasticScheduler)
                                .transform(metricOperator.measure("orchestration.planner.parse"))
                                .onErrorMap(PlanParsingException.class, e -> e)
                );
    }

    private Mono<ExecutionPlan> planFallback(OrchestrationServiceRequest request,
                                             Mono<List<AgentSummaryResponse>> agentSummaries,
                                             Throwable ex) {
        return Mono.error(new IllegalStateException("실행 계획 수립 실패: " + ex.getMessage(), ex));
    }

    /**
     * 프롬프트를 받아 OpenAI에 스트리밍 요청하고, 토큰 단위로 결과를 반환
     */
    @Override
    @Retry(name = "openaiRetry")
    @CircuitBreaker(name = "openaiCB")
    @RateLimiter(name = "openaiRL")
    public Flux<String> streamCompletion(Mono<String> promptMono) {
        return promptMono.flatMapMany(prompt -> {
            Map<String, Object> body = createRequestBody(prompt, true);

            return webClient.post()
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, c -> c.createException().flatMap(Mono::error))
                    .bodyToFlux(DataBuffer.class)
                    .map(db -> {
                        String chunk = db.toString(StandardCharsets.UTF_8);
                        DataBufferUtils.release(db);
                        return chunk;
                    })
                    .transform(this::parseSseStream) // 수정된 파서 적용
                    .doOnNext(tok -> log.debug("[LLM-STREAM] token='{}'", tok.replace("\n", "\\n")))
                    .doOnComplete(() -> log.debug("[LLM-STREAM] completed"))
                    .doOnError(e -> log.error("[LLM-STREAM] error: {}", e.getMessage(), e));
        });
    }

    /**
     *  SSE 스트림을 안정적으로 파싱하여 토큰만 추출하는 Flux Transformer
     */
    private Flux<String> parseSseStream(Flux<String> inbound) {
        return inbound
                .bufferUntil(s -> s.contains("\n\n"))
                .map(list -> String.join("", list))
                .flatMap(block -> Flux.fromArray(block.split("\n\n")))
                .filter(StringUtils::hasText)
                .flatMap(this::extractTokenFromSseBlock);
    }

    /**
     *  단일 SSE 이벤트 블록에서 data 라인을 파싱하여 토큰을 추출합니다.
     */
    private Mono<String> extractTokenFromSseBlock(String eventBlock) {
        return Flux.fromArray(eventBlock.split("\n"))
                .filter(line -> line.startsWith("data:"))
                .map(line -> line.substring(5).stripLeading())
                .filter(data -> !"[DONE]".equals(data))
                .filter(StringUtils::hasText)
                .next()
                .flatMap(this::parseTokenFromJson);
    }

    /**
     *  JSON 문자열에서 실제 토큰(content)을 안전하게 추출합니다.
     */
    private Mono<String> parseTokenFromJson(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            JsonNode contentNode = root.path("choices").get(0).path("delta").path("content");
            if (contentNode != null && !contentNode.isNull()) {
                return Mono.just(contentNode.asText());
            }
        } catch (JsonProcessingException | NullPointerException e) {
            log.warn("[LLM-STREAM] SSE JSON 파싱 실패, chunk: {}", jsonData, e);
        }
        return Mono.empty();
    }

    /**
     * Request Body 생성 메소드
     */
    private Map<String, Object> createRequestBody(String prompt, boolean isStream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        if (isStream) {
            body.put("stream", true);
        }
        return body;
    }

    /**
     * OpenAI에 프롬프트를 보내고 응답 텍스트 추출
     */
    private Mono<String> getCompletion(Mono<String> promptMono) {
        return promptMono.flatMap(prompt -> {
            Map<String, Object> body = createRequestBody(prompt, false);

            Mono<String> httpChain = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            c -> c.createException().flatMap(Mono::error))
                    .bodyToMono(JsonNode.class)
                    .timeout(operatorTimeout)
                    .map(resp -> {
                        JsonNode choices = Optional.ofNullable(resp)
                                .map(r -> r.path("choices"))
                                .orElseThrow(() -> new IllegalStateException("OpenAI 응답이 null입니다."));
                        if (!choices.isArray() || choices.isEmpty()) {
                            throw new IllegalStateException("No choices in OpenAI response");
                        }
                        return choices.get(0).path("message").path("content").asText();
                    })
                    .transform(metricOperator.measure("orchestration.planner.http"));

            return httpChain;
        });
    }
}
