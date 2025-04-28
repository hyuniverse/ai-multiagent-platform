package com.infobank.multiagentplatform.resilience.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.resilience4j.core.IntervalFunction;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = ResilienceConfig.class,
        properties = {
                "resilience.retry.maxAttempts=7",
                "resilience.retry.initialInterval=2000",
                "resilience.retry.multiplier=1.5",
                "resilience.circuitbreaker.failureRateThreshold=70",
                "resilience.circuitbreaker.waitDurationInOpenState=120000",
                "resilience.circuitbreaker.slidingWindowSize=30",
                "resilience.circuitbreaker.permittedNumberOfCallsInHalfOpenState=3"
        }
)
class ResiliencePropertiesBindingTest {

    @Autowired
    private RetryProperties retryProps;

    @Autowired
    private CircuitBreakerProperties cbProps;

    @Test
    void retryPropertiesBinding() {
        assertEquals(7, retryProps.getMaxAttempts());
        assertEquals(2000L, retryProps.getInitialInterval());
        assertEquals(1.5, retryProps.getMultiplier());
    }

    @Test
    void circuitBreakerPropertiesBinding() {
        assertEquals(70f, cbProps.getFailureRateThreshold());
        assertEquals(120000L, cbProps.getWaitDurationInOpenState());
        assertEquals(30, cbProps.getSlidingWindowSize());
        assertEquals(3, cbProps.getPermittedNumberOfCallsInHalfOpenState());
    }
}
