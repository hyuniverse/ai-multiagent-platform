package com.infobank.multiagentplatform.resilience.registry.impl;

import com.infobank.multiagentplatform.resilience.config.ResilienceConfig;
import com.infobank.multiagentplatform.resilience.registry.ResilienceRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig   // <-- SpringExtension + @ContextConfiguration 조합
@ContextConfiguration(
        classes = {
                ResilienceConfig.class,
                ResilienceRegistryImpl.class
        }
)

public class ResilienceRegistryImplTest {

    @Autowired
    private ResilienceRegistry registry;

    @Autowired
    private RetryConfig retryConfig;

    @Autowired
    private CircuitBreakerConfig circuitBreakerConfig;

    @Test
    void defaultRetryConfigRegistered() {
        assertSame(retryConfig, registry.getRetryConfig("default"));
    }

    @Test
    void defaultCircuitBreakerConfigRegistered() {
        assertSame(circuitBreakerConfig, registry.getCircuitBreakerConfig("default"));
    }


    @Test
    void customConfigCanBeAddedAndRetrieved() {
        // 새로운 RetryConfig 생성 및 등록
        RetryConfig customRetry = RetryConfig.custom()
                .maxAttempts(10)
                .build();
        RetryRegistry retryRegistry = registry.getRetryRegistry();
        retryRegistry.addConfiguration("customRetry", customRetry);

        // newly added config should be returned
        RetryConfig retrievedRetry = registry.getRetryConfig("customRetry");
        assertNotNull(retrievedRetry);
        assertSame(customRetry, retrievedRetry);

        // 새로운 CircuitBreakerConfig 생성 및 등록
        CircuitBreakerConfig customCb = CircuitBreakerConfig.custom()
                .failureRateThreshold(10f)
                .build();
        CircuitBreakerRegistry cbRegistry = registry.getCircuitBreakerRegistry();
        cbRegistry.addConfiguration("customCb", customCb);

        // newly added config should be returned
        CircuitBreakerConfig retrievedCb = registry.getCircuitBreakerConfig("customCb");
        assertNotNull(retrievedCb);
        assertSame(customCb, retrievedCb);
    }
}
