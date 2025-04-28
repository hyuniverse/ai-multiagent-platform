package com.infobank.multiagentplatform.resilience.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({ RetryProperties.class, CircuitBreakerProperties.class })
public class ResilienceConfig {

    /**
     * RetryConfig 빈 생성
     */
    @Bean
    public RetryConfig retryConfig(RetryProperties props) {
        IntervalFunction intervalFn = IntervalFunction.ofExponentialBackoff(
                props.getInitialInterval(), props.getMultiplier());
        return RetryConfig.custom()
                .maxAttempts(props.getMaxAttempts())
                .intervalFunction(intervalFn)
                .build();
    }

    /**
     * CircuitBreakerConfig 빈 생성
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig(CircuitBreakerProperties props) {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(props.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(props.getWaitDurationInOpenState()))
                .slidingWindowSize(props.getSlidingWindowSize())
                .permittedNumberOfCallsInHalfOpenState(props.getPermittedNumberOfCallsInHalfOpenState())
                .build();
    }

    /**
     * RetryRegistry 빈 생성 및 기본 정책 등록
     */
    @Bean
    public RetryRegistry retryRegistry(RetryConfig retryConfig) {
        RetryRegistry registry = RetryRegistry.of(retryConfig);
//        registry.addConfiguration("default", retryConfig);
        return registry;
    }

    /**
     * CircuitBreakerRegistry 빈 생성 및 기본 정책 등록
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig cbConfig) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(cbConfig);
//        registry.addConfiguration("default", cbConfig);
        return registry;
    }
}
