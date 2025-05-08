package com.infobank.multiagentplatform.orchestrator.service.executor;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.request.AgentInvocationRequest;
import com.infobank.multiagentplatform.orchestrator.model.plan.AgentTask;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingleTaskExecutorTest {

    @Mock
    private AgentInvokerFactory factory;

    @Mock
    private AgentInvoker invoker;

    @InjectMocks
    private SingleTaskExecutor executor;

    @Test
    @DisplayName("입력 없을 때 goal을 payload로 사용")
    void execute_withoutInputFrom_usesGoalAsPayload() {
        // given
        String agentId = "agent1";
        AgentTask task = mock(AgentTask.class);
        when(task.getAgentId()).thenReturn(agentId);
        when(task.getGoal()).thenReturn("my-goal");
        when(task.getInputFrom()).thenReturn(null);

        AgentDetailResponse meta = mock(AgentDetailResponse.class);
        when(meta.getEndpoint()).thenReturn("http://endpoint");
        when(meta.getProtocol()).thenReturn("REST");
        when(meta.isHasMemory()).thenReturn(false);

        Map<String, AgentDetailResponse> metadataMap = Map.of(agentId, meta);
        Map<String, AgentResult> prevResults = new HashMap<>();

        when(factory.getInvoker("rest")).thenReturn(invoker);
        AgentResult stubResult = new AgentResult("raw-response", "parsed-response");
        doReturn(stubResult).when(invoker).invoke(any(AgentInvocationRequest.class));

        // when
        AgentResult result = executor.execute(task, metadataMap, prevResults);

        // then
        ArgumentCaptor<AgentInvocationRequest> captor = ArgumentCaptor.forClass(AgentInvocationRequest.class);
        verify(invoker).invoke(captor.capture());
        AgentInvocationRequest call = captor.getValue();

        assertEquals(agentId, call.getAgentId());
        assertEquals("http://endpoint", call.getEndpoint());
        assertEquals(ProtocolType.REST, call.getProtocol());
        assertEquals("my-goal", call.getPayload());
        assertFalse(call.isHasMemory());

        assertEquals("raw-response", result.getRawResponse());
        assertEquals("parsed-response", result.getParsedResult());
    }

    @Test
    @DisplayName("입력 있을 때 이전 결과를 payload로 사용")
    void execute_withInputFrom_usesPreviousResultAsPayload() {
        // given
        AgentTask task = mock(AgentTask.class);
        when(task.getAgentId()).thenReturn("agent2");
        when(task.getInputFrom()).thenReturn("agent1");
        when(task.getGoal()).thenReturn("ignored");

        AgentDetailResponse meta = mock(AgentDetailResponse.class);
        when(meta.getEndpoint()).thenReturn("http://ep2");
        when(meta.getProtocol()).thenReturn("MCP");
        when(meta.isHasMemory()).thenReturn(true);

        Map<String, AgentDetailResponse> metadataMap = Map.of("agent2", meta);
        Map<String, AgentResult> prevResults = Map.of(
                "agent1", new AgentResult("prevRaw", "prevParsed")
        );

        when(factory.getInvoker("mcp")).thenReturn(invoker);
        AgentResult stubResult = new AgentResult("raw2", "parsed2");
        doReturn(stubResult).when(invoker).invoke(any(AgentInvocationRequest.class));

        // when
        AgentResult result = executor.execute(task, metadataMap, new HashMap<>(prevResults));

        // then
        ArgumentCaptor<AgentInvocationRequest> captor = ArgumentCaptor.forClass(AgentInvocationRequest.class);
        verify(invoker).invoke(captor.capture());
        AgentInvocationRequest call = captor.getValue();

        assertEquals("prevParsed", call.getPayload());
        assertTrue(call.isHasMemory());
        assertEquals(ProtocolType.MCP, call.getProtocol());

        assertEquals("raw2", result.getRawResponse());
        assertEquals("parsed2", result.getParsedResult());
    }
}
