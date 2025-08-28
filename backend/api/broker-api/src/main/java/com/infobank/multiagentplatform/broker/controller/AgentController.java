package com.infobank.multiagentplatform.broker.controller;

import com.infobank.multiagentplatform.broker.controller.request.AgentRegisterRequest;
import com.infobank.multiagentplatform.broker.controller.request.AgentUpdateRequest;
import com.infobank.multiagentplatform.broker.service.AgentService;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentUpdateResponse;
import com.infobank.multiagentplatform.commons.api.ApiResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.infobank.multiagentplatform.commons.api.ApiResponse.created;
import static com.infobank.multiagentplatform.commons.api.ApiResponse.ok;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "에이전트 등록/관리 API")
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "에이전트 등록", description = "에이전트를 등록합니다. ID가 중복되면 409 에러를 반환합니다.")
    @Timed(value = "agent.register", description = "Time to register agent")
    public Mono<ApiResponse<AgentRegisterResponse>> registerAgent(@Valid @RequestBody AgentRegisterRequest request) {
        return agentService.registerAgent(request.toServiceRequest())
                .map(response -> created(response));
    }


    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "에이전트 수정", description = "에이전트를 수정합니다. ID가 없으면 404를 반환합니다.")
    @Timed(value = "agent.update", description = "Time to update agent")
    public Mono<ApiResponse<AgentUpdateResponse>> updateAgent(
            @PathVariable String uuid,
            @Valid @RequestBody AgentUpdateRequest request) {

        return agentService.updateAgent(uuid, request.toServiceRequest())
                .map(response -> ok(response));
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "에이전트 삭제", description = "에이전트를 삭제합니다. ID가 없으면 404를 반환합니다.")
    @Timed(value = "agent.delete", description = "Time to delete agent")
    public Mono<ApiResponse<Void>> deleteAgent(@PathVariable String uuid) {
        return agentService.deleteAgent(uuid)
                .then(Mono.fromCallable(() -> ok(null)));
    }
}