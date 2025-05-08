package com.infobank.multiagentplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
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