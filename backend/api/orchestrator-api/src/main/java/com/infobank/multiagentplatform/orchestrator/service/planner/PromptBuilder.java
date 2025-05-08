package com.infobank.multiagentplatform.orchestrator.service.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import com.infobank.multiagentplatform.orchestrator.service.request.OrchestrationServiceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 프롬프트 템플릿 기반으로 LLM 호출용 입력 생성
 */
@Component
public class PromptBuilder {
    private final ObjectMapper objectMapper;
    private final ObjectNode templateRoot;

    public PromptBuilder(ObjectMapper om,
                         @Value("classpath:prompt-templates/default-prompt.json") Resource res)
            throws IOException {
        this.objectMapper = om;
        this.templateRoot = (ObjectNode) om.readTree(res.getInputStream());
    }

    /**
     * 사용자 요청과 에이전트 요약 DTO를 조합하여 프롬프트 JSON 문자열 생성
     */
    public String buildPrompt(OrchestrationServiceRequest req, List<AgentSummaryResponse> agents) {
        // 원본 템플릿 복제
        ObjectNode root = templateRoot.deepCopy();
        root.put("user_input", req.getRawText());

        ArrayNode arr = objectMapper.createArrayNode();
        for (AgentSummaryResponse a : agents) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("agentId", a.getUuid());
            node.put("description", a.getDescription());
            node.putPOJO("inputTypes", a.getInputTypes());
            node.putPOJO("outputTypes", a.getOutputTypes());
            arr.add(node);
        }
        root.set("agent_list", arr);

        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("프롬프트 JSON 생성 실패", e);
        }
    }
}
