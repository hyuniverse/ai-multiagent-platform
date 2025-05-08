//package com.infobank.multiagentplatform.orchestrator.planner;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
//import com.infobank.multiagentplatform.domain.agent.model.AgentSummary;
//import com.infobank.multiagentplatform.orchestrator.service.request.StandardRequest;
//import com.infobank.multiagentplatform.orchestrator.service.planner.PromptBuilder;
//import org.junit.jupiter.api.Test;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class PromptBuilderTest {
//
//    @Test
//    void buildPrompt_shouldGenerateValidJson_withUserInputAndAgentList() throws Exception {
//        // given
//        ObjectMapper om = new ObjectMapper();
//        Resource res = new ClassPathResource("prompt-templates/default-prompt.json");
//        PromptBuilder promptBuilder = new PromptBuilder(om, res);
//
//        StandardRequest request = StandardRequest.builder()
//                .rawText("Summarize the report")
//                .inputType("text")
//                .build();
//
//        AgentSummary agent1 = AgentSummary.builder()
//                .agentId("agent-1")
//                .description("summarizer")
//                .inputType("text")
//                .outputType("summary")
//                .build();
//
//        AgentSummary agent2 = AgentSummary.builder()
//                .agentId("agent-2")
//                .description("translator")
//                .inputType("text")
//                .outputType("ko-text")
//                .build();
//
//        List<AgentSummaryResponse> agentList = List.of(agent1, agent2);
//
//        // when
//        String resultJson = promptBuilder.buildPrompt(request, agentList);
//
//        // then
//        JsonNode node = om.readTree(resultJson);
//        System.out.println("🧾 Generated Prompt:\n" + resultJson);
//
//        assertThat(node.get("user_input").asText()).isEqualTo("Summarize the report");
//        assertThat(node.get("agent_list")).hasSize(2);
//
//        JsonNode firstAgent = node.get("agent_list").get(0);
//        assertThat(firstAgent.get("agentId").asText()).isEqualTo(agent1.getAgentId());
//        assertThat(firstAgent.get("description").asText()).isEqualTo(agent1.getDescription());
//
//        JsonNode secondAgent = node.get("agent_list").get(1);
//        assertThat(secondAgent.get("agentId").asText()).isEqualTo(agent2.getAgentId());
//        assertThat(secondAgent.get("description").asText()).isEqualTo(agent2.getDescription());
//
//        assertThat(node.get("response_format")).isNotNull();
//        assertThat(node.get("response_format").get("schema")).isNotNull();
//    }
//}
