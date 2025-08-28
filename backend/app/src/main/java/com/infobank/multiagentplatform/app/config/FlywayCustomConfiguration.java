package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class FlywayCustomConfiguration {

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer(Environment environment) {
        return configuration -> {
            log.info("=== Flyway Configuration Customizer ===");
            
            String jdbcUrl = environment.getProperty("SPRING_JDBC_URL");
            String username = environment.getProperty("SPRING_DATASOURCE_USERNAME");
            String password = environment.getProperty("SPRING_DATASOURCE_PASSWORD");
            
            log.info("From Environment - JDBC URL: {}", jdbcUrl);
            log.info("From Environment - Username: {}", username);
            log.info("From Environment - Password provided: {}", password != null && !password.isEmpty());
            
            log.info("Flyway config before customization:");
            log.info("- URL: {}", configuration.getUrl());
            log.info("- Username: {}", configuration.getUser());
            log.info("- Password provided: {}", configuration.getPassword() != null && !configuration.getPassword().isEmpty());
            
            // 환경변수가 있으면 명시적으로 설정
            if (jdbcUrl != null && username != null && password != null) {
                log.info("Manually setting Flyway configuration from environment variables");
                configuration.dataSource(jdbcUrl, username, password);
                
                log.info("Flyway config after customization:");
                log.info("- URL: {}", configuration.getUrl());
                log.info("- Username: {}", configuration.getUser());
                log.info("- Password provided: {}", configuration.getPassword() != null && !configuration.getPassword().isEmpty());
            } else {
                log.error("Environment variables missing!");
                log.error("SPRING_JDBC_URL: {}", jdbcUrl);
                log.error("SPRING_DATASOURCE_USERNAME: {}", username);
                log.error("SPRING_DATASOURCE_PASSWORD provided: {}", password != null && !password.isEmpty());
            }
            
            log.info("=== End Flyway Configuration Customizer ===");
        };
    }
}
