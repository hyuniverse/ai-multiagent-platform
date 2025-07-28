package com.infobank.multiagentplatform.broker.controller;

import com.infobank.multiagentplatform.broker.controller.request.AgentRegisterRequest;
import com.infobank.multiagentplatform.broker.controller.request.AgentUpdateRequest;
import com.infobank.multiagentplatform.broker.service.AgentService;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.broker.service.response.AgentUpdateResponse;
import com.infobank.multiagentplatform.commons.api.ApiResponse;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.infobank.multiagentplatform.commons.api.ApiResponse.created;
import static com.infobank.multiagentplatform.commons.api.ApiResponse.ok;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "에이전트 등록/관리 API")
public class AgentController {

    private final AgentService agentService;
    private final ObservationRegistry observationRegistry;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "에이전트 등록", description = "에이전트를 등록합니다. ID가 중복되면 409 에러를 반환합니다.")
    @Timed(value = "agent.register", description = "Time to register agent")
    public ApiResponse<AgentRegisterResponse> registerAgent(@Valid @RequestBody AgentRegisterRequest request) {
        String traceId = MDC.get("traceId");
        MDC.put("traceId", traceId);
        AgentRegisterResponse response = agentService.registerAgent(request.toServiceRequest());

        return created(response);
    }


    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "에이전트 수정", description = "에이전트를 수정합니다. ID가 없으면 404를 반환합니다.")
    @Timed(value = "agent.update", description = "Time to update agent")
    public ApiResponse<AgentUpdateResponse> updateAgent(
            @PathVariable String uuid,
            @Valid @RequestBody AgentUpdateRequest request) {

        MDC.put("traceId", MDC.get("traceId"));
        AgentUpdateResponse response = agentService.updateAgent(uuid, request.toServiceRequest());
        return ok(response);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "에이전트 삭제", description = "에이전트를 삭제합니다. ID가 없으면 404를 반환합니다.")
    @Timed(value = "agent.delete", description = "Time to delete agent")
    public ApiResponse<Void> deleteAgent(@PathVariable String uuid) {
        MDC.put("traceId", MDC.get("traceId"));
        agentService.deleteAgent(uuid);
        return ok(null);
    }

}
