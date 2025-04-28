package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.resilience.annotation.CircuitBreaker;
import com.infobank.multiagentplatform.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class SampleService {

    private int counter = 0;

    @Retryable(name = "default", fallbackMethod = "onError")
    @CircuitBreaker(name = "default", fallbackMethod = "onError")
    public String flaky() {
        if (counter++ < 2) {
            throw new RuntimeException("fail");
        }
        return "ok";
    }

    public String onError(Throwable ex) {
        return "recovered:" + ex.getMessage();
    }
}
