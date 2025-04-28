package com.infobank.multiagentplatform.resilience.registry;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

/**
 * 중앙에서 Retry 및 CircuitBreaker 레지스트리를 관리합니다.
 */
public interface ResilienceRegistry {

    /**
     * RetryRegistry 를 반환합니다.
     */
    RetryRegistry getRetryRegistry();

    /**
     * CircuitBreakerRegistry 를 반환합니다.
     */
    CircuitBreakerRegistry getCircuitBreakerRegistry();

    /**
     * 주어진 이름으로 등록된 RetryConfig 를 반환합니다.
     */
    RetryConfig getRetryConfig(String name);

    /**
     * 주어진 이름으로 등록된 CircuitBreakerConfig 를 반환합니다.
     */
    CircuitBreakerConfig getCircuitBreakerConfig(String name);
}