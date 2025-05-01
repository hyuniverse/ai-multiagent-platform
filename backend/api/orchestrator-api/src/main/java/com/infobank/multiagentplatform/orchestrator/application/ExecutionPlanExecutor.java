package com.infobank.multiagentplatform.orchestrator.application;

import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import com.infobank.multiagentplatform.domain.agent.task.AgentCallTask;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;
import com.infobank.multiagentplatform.invoker.application.AgentInvokerFactory;
import com.infobank.multiagentplatform.orchestrator.dto.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.dto.TaskBlock;
import com.infobank.multiagentplatform.orchestrator.util.AgentCallTaskMapper;
import com.infobank.multiagentplatform.invoker.domain.AgentInvoker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionPlanExecutor {

    private final BrokerClient brokerClient;
    private final AgentInvokerFactory invokerFactory;

    // 블록 내 병렬 실행을 위한 스레드풀
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
    );

    /**
     * ExecutionPlan에 따라 블록 단위로 순차 실행, 블록 내는 병렬로 호출
     * @param plan LLM이 생성한 실행 계획
     * @return taskId -> AgentResult 매핑
     * @throws ExecutionException   개별 호출 실패 시
     * @throws InterruptedException 호출 중 인터럽트 발생 시
     */
    public Map<String, AgentResult> execute(ExecutionPlan plan) throws ExecutionException, InterruptedException {
        // 결과 저장소
        Map<String, AgentResult> results = new ConcurrentHashMap<>();

        // 블록 단위 순차 실행
        for (TaskBlock block : plan.getBlocks()) {
            List<Future<?>> futures = block.getTasks().stream()
                    .map(task -> executor.submit(() -> {
                        String taskId = task.getAgentId();

                        // 1) 메타데이터 조회
                        AgentMetadata meta = brokerClient.getAgentMetadata(task.getAgentId());

                        // 2) 페이로드 결정: 이전 결과가 있으면 parsedResult, 아니면 goal
                        Object inputObj = task.getInputFrom() == null
                                ? task.getGoal()
                                : results.get(task.getInputFrom()).getParsedResult();
                        String payload = Objects.toString(inputObj, "");

                        // 3) AgentCallTask 생성
                        AgentCallTask call = AgentCallTaskMapper.from(task, meta, payload);

                        // 4) Invoker 선택 및 호출
                        AgentInvoker invoker = invokerFactory.getInvoker(
                                meta.getProtocol().name().toLowerCase()
                        );
                        Object rawResponse = invoker.invoke(call);

                        // 5) AgentResult 생성 및 저장
                        AgentResult result = new AgentResult(
                                Objects.toString(rawResponse, ""),
                                rawResponse
                        );
                        results.put(task.getAgentId(), result);
                        return null;
                    })).collect(Collectors.toList());

            // 6) 블록 내 모든 태스크 완료 대기
            for (Future<?> future : futures) {
                future.get();
            }
        }

        return results;
    }
}
