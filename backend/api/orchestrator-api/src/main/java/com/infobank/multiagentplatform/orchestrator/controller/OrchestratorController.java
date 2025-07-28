package com.infobank.multiagentplatform.orchestrator.controller;

import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.orchestrator.controller.request.OrchestrationRequest;
import com.infobank.multiagentplatform.orchestrator.service.OrchestrationService;
import com.infobank.multiagentplatform.orchestrator.service.response.OrchestrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
     * 사용자 요청을 받아 전체 플로우를 실행한 뒤, 최종 DTO를 반환한다.
     */
    @PostMapping("/ask")
    public Mono<ApiResponse<OrchestrationResponse>> ask(@Valid @RequestBody OrchestrationRequest request) {
        logger.info("/ask 요청 수신: {}", request);

        return orchestrationService
                .orchestrate(request.toServiceRequest())
                .map(ApiResponse::ok);
    }
}
