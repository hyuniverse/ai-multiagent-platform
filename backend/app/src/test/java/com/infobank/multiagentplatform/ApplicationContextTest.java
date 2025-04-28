package com.infobank.multiagentplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ApplicationContextTest {

    @Autowired
    ApplicationContext ctx;

    @Test
    void resilienceBeansLoaded() {
        assertThat(ctx.containsBean("retryConfig")).isTrue();
        assertThat(ctx.containsBean("circuitBreakerConfig")).isTrue();
    }
}
