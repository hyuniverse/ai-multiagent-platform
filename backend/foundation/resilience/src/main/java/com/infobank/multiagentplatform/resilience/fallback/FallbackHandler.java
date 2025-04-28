package com.infobank.multiagentplatform.resilience.fallback;

/**
 * 장애 발생 시 대체 로직을 수행하는 핸들러
 *
 * @param <T> 실행 대상 메서드의 반환 타입
 */
public interface FallbackHandler<T> {
    /**
     * @param ex   발생한 예외
     * @param args 원래 메서드에 전달된 인자들
     * @return 대체 결과
     */
    T handle(Throwable ex, Object... args);
}
