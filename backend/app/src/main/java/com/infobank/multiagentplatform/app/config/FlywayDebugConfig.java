package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.stream.Collectors;
@Slf4j
@Configuration
public class FlywayDebugConfig {

    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
    public ApplicationRunner flywayDebugRunner(Flyway flyway) {
        return args -> {
            log.info("=== Flyway Debug Information ===");
            
            // 환경 변수 확인
            log.info("SPRING_JDBC_URL: {}", environment.getProperty("SPRING_JDBC_URL"));
            log.info("SPRING_DATASOURCE_USERNAME: {}", environment.getProperty("SPRING_DATASOURCE_USERNAME"));
            log.info("Database URL from application.yml: {}", environment.getProperty("spring.datasource.url"));
            log.info("Flyway enabled: {}", environment.getProperty("spring.flyway.enabled"));
            log.info("Flyway baseline-on-migrate: {}", environment.getProperty("spring.flyway.baseline-on-migrate"));
            log.info("Flyway locations: {}", environment.getProperty("spring.flyway.locations"));
            
            try {
                // Flyway 정보 확인
                log.info("Flyway configuration:");
                log.info("- Database URL: {}", flyway.getConfiguration().getUrl());
                log.info("- Schema: {}", Arrays.stream(flyway.getConfiguration().getSchemas()).collect(Collectors.joining(", ")));
                log.info("- Locations: {}", Arrays.stream(flyway.getConfiguration().getLocations()).map(Object::toString).collect(Collectors.joining(", ")));
                log.info("- Baseline version: {}", flyway.getConfiguration().getBaselineVersion());
                log.info("- Baseline on migrate: {}", flyway.getConfiguration().isBaselineOnMigrate());
                
                // 마이그레이션 정보 확인
                MigrationInfo[] migrationInfos = flyway.info().all();
                log.info("Total migrations found: {}", migrationInfos.length);
                
                for (MigrationInfo info : migrationInfos) {
                    log.info("Migration: {} - {} - State: {}", 
                        info.getVersion(), 
                        info.getDescription(), 
                        info.getState());
                }
                
                // 현재 상태 확인
                MigrationInfo current = flyway.info().current();
                if (current != null) {
                    log.info("Current migration version: {}", current.getVersion());
                } else {
                    log.warn("No current migration found - database might be empty");
                }
                
                // 수동으로 마이그레이션 실행 (이미 실행된 것은 건너뜀)
                log.info("Attempting to run migrations...");
                int migrationsExecuted = flyway.migrate().migrationsExecuted;
                log.info("Migrations executed: {}", migrationsExecuted);
                
            } catch (Exception e) {
                log.error("Error during Flyway debug check: ", e);
            }
            
            log.info("=== End Flyway Debug Information ===");
        };
    }
}
