package com.infobank.multiagentplatform.orchestrator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 개별 태스크 실행 결과를 담는 DTO
 */
@Getter
@Builder
@ToString
public class TaskExecutionResult {
    /** 태스크 식별자 (AgentTask#getAgentId()) */
    private final String taskId;

    /** 실행 성공 여부 */
    private final boolean success;

    /** 에이전트가 반환한 실제 응답(파싱된 결과) */
    private final Object payload;

    /** 실패 시 예외 또는 에러 메시지 (성공 시 null) */
    private final String errorMessage;

    /** 실행 소요 시간 (밀리초) */
    private final long durationMs;
}
