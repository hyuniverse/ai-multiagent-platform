package com.infobank.multiagentplatform.resilience.registry.impl;

import com.infobank.multiagentplatform.resilience.registry.ResilienceRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * RetryRegistry 및 CircuitBreakerRegistry 를 관리하는 구현체
 */
@Configuration
public class ResilienceRegistryImpl implements ResilienceRegistry {

    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    public ResilienceRegistryImpl(
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.retryRegistry = retryRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public RetryRegistry getRetryRegistry() {
        return retryRegistry;
    }

    @Override
    public CircuitBreakerRegistry getCircuitBreakerRegistry() {
        return circuitBreakerRegistry;
    }

    @Override
    public RetryConfig getRetryConfig(String name) {
        return retryRegistry.getConfiguration(name)
                .orElseGet(retryRegistry::getDefaultConfig);
    }

    @Override
    public CircuitBreakerConfig getCircuitBreakerConfig(String name) {
        return circuitBreakerRegistry.getConfiguration(name)
                .orElseGet(circuitBreakerRegistry::getDefaultConfig);
    }
}
