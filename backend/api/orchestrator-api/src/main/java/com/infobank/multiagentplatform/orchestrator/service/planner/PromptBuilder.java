package com.infobank.multiagentplatform.orchestrator.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.model.StandardRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PromptBuilder {
    private final ObjectMapper objectMapper;
    private final ObjectNode templateRoot;

    public PromptBuilder(ObjectMapper om,
                         @Value("classpath:prompt-templates/default-prompt.json") Resource res)
            throws IOException {
        this.objectMapper = om;
        // 템플릿을 ObjectNode 로 파싱해서 보관
        this.templateRoot = (ObjectNode)om.readTree(res.getInputStream());
    }

    public String buildPrompt(StandardRequest req, List<AgentSummary> agents) {
        // 원본 템플릿 복제
        ObjectNode root = templateRoot.deepCopy();
        // 1) 사용자 입력
        root.put("user_input", req.getRawText());

        // 2) 에이전트 목록
        ArrayNode arr = objectMapper.createArrayNode();
        for (AgentSummary a : agents) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("agentId",   a.getAgentId());
            node.put("description",a.getDescription());
            node.put("inputType",  a.getInputType());
            node.put("outputType", a.getOutputType());
            arr.add(node);
        }
        root.set("agent_list", arr);

        // 3) response_format 은 템플릿에 이미 들어있으므로 건드리지 않음

        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // TODO: PlanParsingException 등으로 던져도 좋고, 폴백 텍스트 빌더로 대체해도 OK
            throw new IllegalStateException("프롬프트 JSON 생성 실패", e);
        }
    }
}
