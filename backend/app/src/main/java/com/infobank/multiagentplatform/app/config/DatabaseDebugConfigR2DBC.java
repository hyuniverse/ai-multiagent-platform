package com.infobank.multiagentplatform.app.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class DatabaseDebugConfigR2DBC {

    @Bean
    public ApplicationRunner databaseDebugRunnerR2DBC(ConnectionFactory connectionFactory, 
                                                     Environment environment,
                                                     DatabaseClient databaseClient) {
        return args -> {
            log.info("=== Database Connection Debug (R2DBC) ===");
            
            // 환경변수 확인
            log.info("SPRING_R2DBC_URL: {}", environment.getProperty("SPRING_R2DBC_URL"));
            log.info("SPRING_JDBC_URL: {}", environment.getProperty("SPRING_JDBC_URL"));
            log.info("R2DBC URL from config: {}", environment.getProperty("spring.r2dbc.url"));
            log.info("JDBC URL from config: {}", environment.getProperty("spring.datasource.url"));
            
            try {
                // R2DBC 연결 테스트
                Mono<String> connectionTest = Mono.from(connectionFactory.create())
                    .flatMap(connection -> {
                        log.info("R2DBC connection successful!");
                        return Mono.fromRunnable(() -> {
                            try {
                                connection.close();
                            } catch (Exception e) {
                                log.warn("Error closing connection: ", e);
                            }
                        }).then(Mono.just("Connected"));
                    })
                    .doOnError(error -> log.error("R2DBC connection failed: ", error));
                
                // 블로킹 방식으로 결과 확인
                String result = connectionTest.block();
                log.info("Connection test result: {}", result);
                
                // 테이블 확인
                log.info("Checking for flyway_schema_history table...");
                databaseClient.sql("SELECT COUNT(*) as count FROM information_schema.tables WHERE table_name = 'flyway_schema_history'")
                    .map(row -> row.get("count", Long.class))
                    .one()
                    .doOnNext(count -> {
                        if (count > 0) {
                            log.info("flyway_schema_history table exists");
                        } else {
                            log.warn("flyway_schema_history table does NOT exist");
                        }
                    })
                    .doOnError(error -> log.error("Error checking flyway table: ", error))
                    .onErrorReturn(0L)
                    .block();
                
                // 모든 테이블 확인
                log.info("Checking all tables...");
                databaseClient.sql("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")
                    .map(row -> row.get("table_name", String.class))
                    .all()
                    .collectList()
                    .doOnNext(tables -> {
                        if (tables.isEmpty()) {
                            log.warn("No tables found in public schema!");
                        } else {
                            log.info("Found {} tables in public schema:", tables.size());
                            tables.forEach(table -> log.info("- {}", table));
                        }
                    })
                    .doOnError(error -> log.error("Error checking tables: ", error))
                    .onErrorReturn(java.util.Collections.emptyList())
                    .block();
                    
            } catch (Exception e) {
                log.error("Database debug failed: ", e);
            }
            
            log.info("=== End Database Debug ===");
        };
    }
}
