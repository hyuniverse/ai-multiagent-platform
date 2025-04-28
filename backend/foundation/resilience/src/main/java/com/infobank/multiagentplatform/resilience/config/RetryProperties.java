package com.infobank.multiagentplatform.resilience.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "resilience.retry")
public class RetryProperties {

    /** 최대 재시도 횟수 */
    @Min(0)
    @NestedConfigurationProperty
    private int maxAttempts = 3;

    /** 최초 대기 시간 (ms) */
    @Min(0)
    @NestedConfigurationProperty
    private long initialInterval = 500L;

    /** 지수 백오프 배수 */
    @NestedConfigurationProperty
    private double multiplier = 2.0;

    // getters / setters

    public int getMaxAttempts() {
        return maxAttempts;
    }
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    public long getInitialInterval() {
        return initialInterval;
    }
    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
