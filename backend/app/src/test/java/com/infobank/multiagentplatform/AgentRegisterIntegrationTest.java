package com.infobank.multiagentplatform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.app.MultiAgentPlatformApplication;
import com.infobank.multiagentplatform.broker.dto.AgentRegisterRequest;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MultiAgentPlatformApplication.class)
@AutoConfigureMockMvc
class AgentRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("에이전트 등록 성공")
    void agent_register_success() throws Exception {
        AgentRegisterRequest request = AgentRegisterRequest.builder()
                .id("agent-001")
                .type("react")
                .protocol(ProtocolType.REST)
                .endpoint("http://localhost:8081/invoke")
                .hasMemory(true)
                .memoryType("redis")
                .inputTypes(List.of("text"))
                .outputTypes(List.of("TEXT"))
                .description("테스트용 에이전트")
                .build();

        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
