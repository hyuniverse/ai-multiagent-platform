package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class EnvironmentDebugConfig {

    @Bean
    public ApplicationRunner environmentDebugRunner(Environment environment) {
        return args -> {
            log.info("=== Environment Variables Debug ===");
            
            // 환경변수 직접 확인
            log.info("SPRING_JDBC_URL from env: {}", System.getenv("SPRING_JDBC_URL"));
            log.info("SPRING_R2DBC_URL from env: {}", System.getenv("SPRING_R2DBC_URL"));
            log.info("SPRING_DATASOURCE_USERNAME from env: {}", System.getenv("SPRING_DATASOURCE_USERNAME"));
            log.info("SPRING_DATASOURCE_PASSWORD from env: {}", System.getenv("SPRING_DATASOURCE_PASSWORD"));
            
            // Spring Environment에서 확인
            log.info("SPRING_JDBC_URL from spring: {}", environment.getProperty("SPRING_JDBC_URL"));
            log.info("SPRING_R2DBC_URL from spring: {}", environment.getProperty("SPRING_R2DBC_URL"));
            log.info("SPRING_DATASOURCE_USERNAME from spring: {}", environment.getProperty("SPRING_DATASOURCE_USERNAME"));
            log.info("SPRING_DATASOURCE_PASSWORD from spring: {}", environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
            
            // Resolved properties 확인
            log.info("spring.datasource.url: {}", environment.getProperty("spring.datasource.url"));
            log.info("spring.datasource.username: {}", environment.getProperty("spring.datasource.username"));
            log.info("spring.datasource.password: {}", environment.getProperty("spring.datasource.password"));
            
            log.info("spring.r2dbc.url: {}", environment.getProperty("spring.r2dbc.url"));
            log.info("spring.r2dbc.username: {}", environment.getProperty("spring.r2dbc.username"));
            log.info("spring.r2dbc.password: {}", environment.getProperty("spring.r2dbc.password"));
            
            log.info("spring.flyway.url: {}", environment.getProperty("spring.flyway.url"));
            log.info("spring.flyway.username: {}", environment.getProperty("spring.flyway.username"));
            log.info("spring.flyway.password: {}", environment.getProperty("spring.flyway.password"));
            
            log.info("=== End Environment Debug ===");
        };
    }
}
