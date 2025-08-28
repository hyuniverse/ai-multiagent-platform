package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class FlywayManualExecutor {

    @Bean
    public ApplicationRunner flywayManualRunner(Environment environment) {
        return args -> {
            log.info("=== Manual Flyway Execution ===");
            
            // 환경변수 확인
            String jdbcUrl = environment.getProperty("SPRING_JDBC_URL");
            String username = environment.getProperty("SPRING_DATASOURCE_USERNAME");
            String password = environment.getProperty("SPRING_DATASOURCE_PASSWORD");
            
            log.info("JDBC URL: {}", jdbcUrl);
            log.info("Username: {}", username);
            log.info("Password provided: {}", password != null && !password.isEmpty());
            
            if (jdbcUrl == null || username == null || password == null) {
                log.error("Database connection parameters are missing!");
                log.error("SPRING_JDBC_URL: {}", jdbcUrl);
                log.error("SPRING_DATASOURCE_USERNAME: {}", username);
                log.error("SPRING_DATASOURCE_PASSWORD: {}", password != null ? "[HIDDEN]" : "null");
                return;
            }
            
            try {
                // 수동으로 Flyway 인스턴스 생성
                Flyway flyway = Flyway.configure()
                    .dataSource(jdbcUrl, username, password)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("1")
                    .validateOnMigrate(true)
                    .cleanDisabled(true)
                    .load();
                
                log.info("Manual Flyway instance created successfully");
                log.info("Flyway configuration - URL: {}", flyway.getConfiguration().getUrl());
                log.info("Flyway configuration - Locations: {}", 
                    java.util.Arrays.stream(flyway.getConfiguration().getLocations())
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining(", ")));
                
                // 현재 상태 확인
                var info = flyway.info();
                var all = info.all();
                log.info("Found {} migrations", all.length);
                
                for (var migration : all) {
                    log.info("Migration: {} - {} - State: {}", 
                        migration.getVersion(), 
                        migration.getDescription(), 
                        migration.getState());
                }
                
                // 마이그레이션 실행
                log.info("Executing migrations...");
                var result = flyway.migrate();
                log.info("Migrations executed: {}", result.migrationsExecuted);
                log.info("Target schema version: {}", result.targetSchemaVersion);
                
                // 실행 후 상태 재확인
                var afterInfo = flyway.info();
                var current = afterInfo.current();
                if (current != null) {
                    log.info("Current migration version after execution: {}", current.getVersion());
                } else {
                    log.warn("No current migration found after execution");
                }
                
            } catch (Exception e) {
                log.error("Manual Flyway execution failed: ", e);
            }
            
            log.info("=== End Manual Flyway Execution ===");
        };
    }
}
