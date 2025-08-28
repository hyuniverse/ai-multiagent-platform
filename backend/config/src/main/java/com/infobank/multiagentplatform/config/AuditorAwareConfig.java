package com.infobank.multiagentplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

@Configuration
public class AuditorAwareConfig {

    @Bean
    public ReactiveAuditorAware<Long> auditorProvider() {
        return () -> Mono.just(1L);
    }
}