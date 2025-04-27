package com.infobank.multiagentplatform.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.invoker.domain.AgentRequest;
import com.infobank.multiagentplatform.invoker.domain.AgentResult;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import com.infobank.multiagentplatform.invoker.exception.UnsupportedProtocolException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AgentInvokerTest {

    @Test
    void invoke_success_with_function_call_agent() throws Exception{
        AgentMetadata metadata = AgentMetadata.builder()
                .id("function-call-agent")
                .type("function-call")
                .protocol(ProtocolType.REST)
                .endpoint("http://localhost:8000/parse")
                .memory(null)  // 빈 값 허용 가정
                .inputTypes(List.of("text"))
                .outputTypes(List.of("json"))
                .description("Function-call 테스트용")
                .build();

        AgentRequest request = new AgentRequest(
                "서울에서 조용한 동네 알려줘",
                new FunctionCallInput("recommend_area", new Args("서울", new String[]{"조용함", "교통 편의"}))
        );

        AgentInvoker invoker = new AgentInvokerFactory().getInvoker(metadata.getProtocol().name());
        AgentResult result = invoker.invoke(metadata, request);
        System.out.println("전송 JSON: " + new ObjectMapper().writeValueAsString(request.getInput()));

        assertNotNull(result);
        assertNotNull(result.getRawResponse());
        System.out.println("응답 결과: " + result.getRawResponse());
    }

    @Test
    void invoke_success_with_geo_agent() throws Exception {
        AgentMetadata metadata = AgentMetadata.builder()
                .id("geo-agent")
                .type("geo-agent")
                .protocol(ProtocolType.REST)
                .endpoint("http://localhost:8001/recommend")
                .inputTypes(List.of("text", "geojson"))
                .outputTypes(List.of("json"))
                .description("지형 기반 상관성 추론 테스트용")
                .build();

        AgentRequest request = new AgentRequest(
                "서울과 인천 사이에 지형적으로 높은 지역은 어디야?",
                new GeoAgentInput(new double[][]{
                        {126.9780, 37.5665}, // 서울
                        {126.7052, 37.4563}  // 인천
                })
        );

        AgentInvoker invoker = new AgentInvokerFactory().getInvoker(metadata.getProtocol().name());
        AgentResult result = invoker.invoke(metadata, request);

        assertNotNull(result);
        System.out.println("Geo Agent 응답: " + result.getRawResponse());
    }

    // 내부 테스트용 입력 클래스
    static class GeoAgentInput {
        private double[][] coordinates;

        public GeoAgentInput(double[][] coordinates) {
            this.coordinates = coordinates;
        }

        public double[][] getCoordinates() {
            return coordinates;
        }
    }


    @Test
    void invoke_throws_when_protocol_is_invalid() {
        AgentMetadata metadata = AgentMetadata.builder()
                .id("invalid-agent")
                .protocol(null) // invalid
                .build();

        AgentRequest request = new AgentRequest("test", null);

        assertThrows(UnsupportedProtocolException.class, () -> {
            new AgentInvokerFactory().getInvoker("invalid");
        });
    }

    // 테스트 입력 구조: Function-Call Agent에 전달될 JSON 형태와 유사
    static class FunctionCallInput {
        public String function;
        public Args args;

        public FunctionCallInput(String function, Args args) {
            this.function = function;
            this.args = args;
        }
    }

    static class Args {
        public String location;
        public String[] filters;

        public Args(String location, String[] filters) {
            this.location = location;
            this.filters = filters;
        }
    }
}
