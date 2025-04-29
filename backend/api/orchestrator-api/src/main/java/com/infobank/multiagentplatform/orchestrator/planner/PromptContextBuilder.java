package com.infobank.multiagentplatform.orchestrator.planner;

import com.infobank.multiagentplatform.orchestrator.dto.StandardRequest;
import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptContextBuilder {

    public String build(StandardRequest request, List<AgentSummary> agentSummaries) {
        StringBuilder prompt = new StringBuilder();

        // 1. 사용자 요청
        prompt.append("User Request:\n")
                .append(request.getRawText())
                .append("\n\n");

        // 2. 사용 가능한 Agent 목록
        prompt.append("Available Agents:\n");
        for (AgentSummary agent : agentSummaries) {
            prompt.append("- ID: ").append(agent.getAgentId()).append("\n")
                    .append("  Description: ").append(agent.getDescription()).append("\n")
                    .append("  InputType: ").append(agent.getInputType()).append("\n")
                    .append("  OutputType: ").append(agent.getOutputType()).append("\n\n");
        }

        // 3. 응답 포맷 요구
        prompt.append("Please return a structured JSON plan with the following format:\n")
                .append("{\n")
                .append("  \"tasks\": [\n")
                .append("    {\n")
                .append("      \"goal\": \"<string>\",\n")
                .append("      \"agentId\": \"<string>\",\n")
                .append("      \"inputType\": \"<string>\",\n")
                .append("      \"inputFrom\": \"<optional-taskId>\"\n")
                .append("    }\n")
                .append("  ],\n")
                .append("  \"unassigned\": [ ... ]\n")
                .append("}\n");

        return prompt.toString();
    }
}
