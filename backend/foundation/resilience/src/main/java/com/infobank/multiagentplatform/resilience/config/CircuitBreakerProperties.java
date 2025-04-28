package com.infobank.multiagentplatform.resilience.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "resilience.circuitbreaker")
public class CircuitBreakerProperties {

    /** 실패율 문턱치 (%) */
    @Min(0) @Max(100)
    private float failureRateThreshold = 50f;

    /** 열림 상태 지속 시간 (ms) */
    @Min(0)
    private long waitDurationInOpenState = 60_000L;

    /** sliding window 크기 */
    @Min(1)
    private int slidingWindowSize = 50;

    /** half-open 상태에서 허용할 호출 수 */
    @Min(1)
    private int permittedNumberOfCallsInHalfOpenState = 5;

    // getters & setters

    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }
    public void setFailureRateThreshold(float failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public long getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }
    public void setWaitDurationInOpenState(long waitDurationInOpenState) {
        this.waitDurationInOpenState = waitDurationInOpenState;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }
    public void setSlidingWindowSize(int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }
    public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) {
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }
}
