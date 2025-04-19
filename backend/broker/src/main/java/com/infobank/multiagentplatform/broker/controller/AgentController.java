package com.infobank.multiagentplatform.broker.controller;

import com.infobank.multiagentplatform.broker.dto.AgentRegisterRequest;
import com.infobank.multiagentplatform.broker.service.register.AgentRegistrationService;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "에이전트 등록/관리 API")
public class AgentController {

    private final AgentRegistrationService registrationService;

    @PostMapping
    @Operation(summary = "에이전트 등록", description = "에이전트를 등록합니다. ID가 중복되면 409 에러를 반환합니다.")
    public ResponseEntity<Void> register(@Validated @RequestBody AgentRegisterRequest request) {
        AgentMetadata metadata = AgentMetadata.builder()
                .id(request.getId())
                .type(request.getType())
                .protocol(request.getProtocol())
                .endpoint(request.getEndpoint())
                .memory(AgentMemory.of(request.getHasMemory(), request.getMemoryType()))
                .inputTypes(request.getInputTypes())
                .outputTypes(request.getOutputTypes())
                .description(request.getDescription())
                .build();

        registrationService.register(metadata);
        return ResponseEntity.created(URI.create("/agents/" + request.getId())).build();
    }
}
