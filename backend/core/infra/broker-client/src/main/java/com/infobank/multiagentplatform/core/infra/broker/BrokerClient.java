package com.infobank.multiagentplatform.core.infra.broker;

import com.infobank.multiagentplatform.core.contract.agent.response.AgentDetailResponse;
import com.infobank.multiagentplatform.core.contract.agent.response.AgentSummaryResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Broker 서비스와의 통신 포트(인터페이스)
 * core/infra 계층에 위치하여 상위(api/orchestrator) 모듈에서 참조만 함
 */
public interface BrokerClient {
    Mono<List<AgentDetailResponse>> getAgentMetadataBatch(List<String> agentIds);
    Mono<List<AgentSummaryResponse>> getAgentSummaries();
}