package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentDebugListener {

    private final Environment environment;

    public EnvironmentDebugListener(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== Environment Variables Debug ===");
        
        // 데이터베이스 관련 환경변수들
        log.info("SPRING_JDBC_URL: {}", environment.getProperty("SPRING_JDBC_URL"));
        log.info("SPRING_R2DBC_URL: {}", environment.getProperty("SPRING_R2DBC_URL"));
        log.info("SPRING_DATASOURCE_USERNAME: {}", environment.getProperty("SPRING_DATASOURCE_USERNAME"));
        log.info("SPRING_DATASOURCE_PASSWORD: {}", 
            environment.getProperty("SPRING_DATASOURCE_PASSWORD") != null ? "***SET***" : "NOT_SET");
        
        // Spring 설정값들
        log.info("spring.datasource.url: {}", environment.getProperty("spring.datasource.url"));
        log.info("spring.r2dbc.url: {}", environment.getProperty("spring.r2dbc.url"));
        log.info("spring.datasource.username: {}", environment.getProperty("spring.datasource.username"));
        log.info("spring.flyway.enabled: {}", environment.getProperty("spring.flyway.enabled"));
        log.info("spring.flyway.baseline-on-migrate: {}", environment.getProperty("spring.flyway.baseline-on-migrate"));
        log.info("spring.flyway.baseline-version: {}", environment.getProperty("spring.flyway.baseline-version"));
        log.info("spring.flyway.locations: {}", environment.getProperty("spring.flyway.locations"));
        log.info("spring.flyway.url: {}", environment.getProperty("spring.flyway.url"));
        
        log.info("=== End Environment Variables Debug ===");
    }
}
