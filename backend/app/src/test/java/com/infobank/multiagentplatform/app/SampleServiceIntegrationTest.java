package com.infobank.multiagentplatform.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SampleServiceIntegrationTest {

    @Autowired
    SampleService sampleService;

    @Test
    void testSuccessAfterRetries() {
        // 처음 두 번은 예외, 세 번째에는 "ok"를 반환해야 합니다.
        String result = sampleService.flaky();
        assertEquals("ok", result);
    }
}
