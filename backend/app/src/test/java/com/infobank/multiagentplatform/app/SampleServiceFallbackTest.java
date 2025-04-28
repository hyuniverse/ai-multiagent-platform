package com.infobank.multiagentplatform.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        // 재시도 횟수를 2회로 줄여서 빠르게 Fallback 경로로 진입
        "resilience.retry.maxAttempts=2"
})
class SampleServiceFallbackTest {

    @Autowired
    SampleService sampleService;

    @Test
    void testFallbackAfterMaxRetries() {
        // counter = 0 → 1st fail, retry → 2nd fail → maxAttempts 초과 → onError 호출
        String result = sampleService.flaky();
        assertEquals("recovered:fail", result);
    }
}
