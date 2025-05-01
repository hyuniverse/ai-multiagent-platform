package com.infobank.multiagentplatform.orchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Orchestrator 모듈 전용 설정
 */
@Configuration
public class OrchestratorConfig {

    /**
     * BrokerClient 및 OpenAIClientImpl 에서 사용할 RestTemplate 빈 정의
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${orchestrator.http.connect-timeout:2000}") Duration connectTimeout,
                                     @Value("${orchestrator.http.read-timeout:10000}") Duration readTimeout) {
        return builder
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout((int) connectTimeout.toMillis());
                    factory.setReadTimeout((int) readTimeout.toMillis());
                    return factory;
                })
                .build();
    }
}