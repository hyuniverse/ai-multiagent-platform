package com.infobank.multiagentplatform.orchestrator.util;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.domain.agent.type.valuetype.AgentMemory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AgentCallTaskMapperTest {

    @DisplayName("메모리 정보를 갖고 있는 agentTask를 agentCallTask에 매핑한다.")
    @Test
    void agentCallTaskMapperTestWithMemory() {
        // given
        AgentMemory memory = AgentMemory.builder()
                .hasMemory(true)
                .memoryType("redis")
                .build();
        AgentMetadata metadata = AgentMetadata.builder()
                .endpoint("http://...")
                .protocol(ProtocolType.REST)
                .memory(memory)
                .build();
        AgentTask planTask = AgentTask.builder()
                .goal("Repeat 'Hello World!'")
                .build();
        String payload = "{\"key\":\"value\"}";

        // when
        AgentCallTask result = AgentCallTaskMapper.from(planTask, metadata, payload);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("agentId", planTask.getAgentId())
                .hasFieldOrPropertyWithValue("endpoint", metadata.getEndpoint())
                .hasFieldOrPropertyWithValue("protocol", metadata.getProtocol())
                .hasFieldOrPropertyWithValue("payload", payload)
                .hasFieldOrPropertyWithValue("hasMemory", memory.isHasMemory());
    }
}