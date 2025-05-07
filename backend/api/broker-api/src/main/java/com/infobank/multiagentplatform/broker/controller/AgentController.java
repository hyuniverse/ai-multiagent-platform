package com.infobank.multiagentplatform.broker.controller;

import com.infobank.multiagentplatform.broker.controller.request.AgentRegisterRequest;
import com.infobank.multiagentplatform.broker.service.AgentService;
import com.infobank.multiagentplatform.broker.service.response.AgentRegisterResponse;
import com.infobank.multiagentplatform.commons.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.infobank.multiagentplatform.commons.api.ApiResponse.created;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "에이전트 등록/관리 API")
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "에이전트 등록", description = "에이전트를 등록합니다. ID가 중복되면 409 에러를 반환합니다.")
    public ApiResponse<AgentRegisterResponse> registerAgent(@Valid @RequestBody AgentRegisterRequest request) {
        AgentRegisterResponse response = agentService.registerAgent(request.toServiceRequest());

        return created(response);
    }


//
//    @PutMapping("/{id}")
//    @Operation(summary = "에이전트 수정")
//    public ResponseEntity<AgentResponse> modifyAgent(
//            @PathVariable String id,
//            @Validated @RequestBody AgentUpdateRequest req) {
//        AgentResponse response = agentService.findById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "에이전트 삭제")
//    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
//        agentService.delete(id);
//        return ResponseEntity.noContent().build();
//    }


//
//    @PostMapping("/batch")
//    @Operation(summary = "배치 조회", description = "여러 agentId로 메타데이터를 한 번에 조회합니다.")
//    public ResponseEntity<List<AgentResponse>> batch(
//            @RequestBody AgentBatchRequest req) {
//        List<AgentResponse> responses = agentService.findBatch(req.getIds());
//        return ResponseEntity.ok(responses);
//    }
}
