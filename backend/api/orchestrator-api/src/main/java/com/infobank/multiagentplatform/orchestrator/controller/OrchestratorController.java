package com.infobank.multiagentplatform.orchestrator.controller;

import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;
import com.infobank.multiagentplatform.orchestrator.service.OrchestrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Orchestrator 엔드포인트 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/orchestrator")
@Validated
@RequiredArgsConstructor
public class OrchestratorController {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorController.class);

    private final OrchestrationService orchestrationService;

    /**
     * 사용자 요청을 받아 전체 플로우를 실행한 뒤, 토큰/청크 단위 SSE를 스트리밍한다.
     */
    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> ask(@Valid @RequestBody OrchestrationRequest request) {
        logger.info("/ask 요청 수신: {}", request);

        return orchestrationService
                .orchestrate(request.toServiceRequest())
                .map(token -> ServerSentEvent.<String>builder(token)
                        .event("chunk")
                        .build())
                .doOnNext(t -> logger.debug("SSE chunk: {}", t.data()))
                .concatWith(Flux.just(ServerSentEvent.<String>builder("[DONE]").event("done").build()))
                .doOnError(error -> logger.error("SSE 스트림 처리 중 오류 발생: {}", error.getMessage(), error));
    }
}
