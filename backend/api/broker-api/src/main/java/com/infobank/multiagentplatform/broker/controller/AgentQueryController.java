package com.infobank.multiagentplatform.broker.controller;

import com.infobank.multiagentplatform.broker.service.AgentQueryService;
import com.infobank.multiagentplatform.commons.api.ApiResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentBatchRequest;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "에이전트 등록/관리 API")
public class AgentQueryController {

    private final AgentQueryService agentQueryService;

    @GetMapping
    @Operation(summary = "전체 에이전트 조회")
    public Mono<ApiResponse<List<AgentDetailResponse>>> listAllAgentDetails() { // Mono로 감싸기
        return agentQueryService.getAllAgentDetails()
                .map(ApiResponse::ok);
    }

    @GetMapping("/{id}")
    @Operation(summary = "단일 에이전트 조회")
    public Mono<ApiResponse<AgentDetailResponse>> getOneAgent(@PathVariable String id) { // Mono로 감싸기
        return agentQueryService.getAgentDetails(id)
                .map(ApiResponse::ok);
    }

    @GetMapping("/summaries")
    @Operation(summary = "사용가능한 에이전트 요약 정보 조회")
    public Mono<ApiResponse<List<AgentSummaryResponse>>> getAgentSummaries() { // Mono로 감싸기
        return agentQueryService.getAvailableAgentSummaries()
                .map(ApiResponse::ok);
    }

    @PostMapping("/batch")
    @Operation(summary = "배치 조회", description = "여러 agentId로 메타데이터를 한 번에 조회합니다.")
    public Mono<ApiResponse<List<AgentDetailResponse>>> getAgentBatch(@Valid @RequestBody AgentBatchRequest request) { // Mono로 감싸기
        return agentQueryService.getAgentDetailsBatch(request.getIds())
                .map(ApiResponse::ok);
    }
}