package com.infobank.multiagentplatform.orchestrator.application;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.orchestrator.exception.PlanParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptContextBuilder {

    private final ObjectMapper objectMapper;
    private final String template;

    public PromptContextBuilder(ObjectMapper objectMapper,
                                @Value("classpath:prompt-templates/orchestrator-prompt.json") Resource resource) throws IOException {
        this.objectMapper = objectMapper;
        this.template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 사용자 요청과 에이전트 목록을 바탕으로 최종 LLM 프롬프트를 생성한다.
     */
    public String buildPrompt(StandardRequest request, List<AgentSummary> agents) {
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(template);
            // 사용자 입력 삽입
            root.put("user_input", request.getRawText());
            // 에이전트 목록 생성
            ArrayNode agentsArray = objectMapper.createArrayNode();
            for (AgentSummary agent : agents) {
                ObjectNode agentNode = objectMapper.createObjectNode();
                agentNode.put("agentId", agent.getAgentId());
                agentNode.put("description", agent.getDescription());
                agentNode.put("inputType", agent.getInputType());
                agentNode.put("outputType", agent.getOutputType());
                agentsArray.add(agentNode);
            }
            root.set("agent_list", agentsArray);
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to build prompt JSON", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read prompt template", e);
        }
    }
}
