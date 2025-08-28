package com.infobank.multiagentplatform.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

@Slf4j
public class EnvironmentLoggingInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        log.info("=== Environment Properties Early Debug ===");
        
        // 시스템 환경변수 직접 확인
        log.info("System.getenv SPRING_JDBC_URL: {}", System.getenv("SPRING_JDBC_URL"));
        log.info("System.getenv SPRING_DATASOURCE_USERNAME: {}", System.getenv("SPRING_DATASOURCE_USERNAME"));
        log.info("System.getenv SPRING_DATASOURCE_PASSWORD: {}", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        
        // PropertySources 확인
        MutablePropertySources propertySources = environment.getPropertySources();
        log.info("Available PropertySources:");
        for (PropertySource<?> propertySource : propertySources) {
            log.info("- {}: {}", propertySource.getName(), propertySource.getClass().getSimpleName());
        }
        
        // Spring Environment에서 해석된 값 확인
        log.info("Environment resolved SPRING_JDBC_URL: {}", environment.getProperty("SPRING_JDBC_URL"));
        log.info("Environment resolved SPRING_DATASOURCE_USERNAME: {}", environment.getProperty("SPRING_DATASOURCE_USERNAME"));
        log.info("Environment resolved SPRING_DATASOURCE_PASSWORD: {}", environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
        
        // 실제 Spring property 확인
        log.info("Resolved spring.datasource.url: {}", environment.getProperty("spring.datasource.url"));
        log.info("Resolved spring.datasource.username: {}", environment.getProperty("spring.datasource.username"));
        log.info("Resolved spring.datasource.password: {}", environment.getProperty("spring.datasource.password"));
        
        log.info("Resolved spring.flyway.url: {}", environment.getProperty("spring.flyway.url"));
        log.info("Resolved spring.flyway.username: {}", environment.getProperty("spring.flyway.username"));
        log.info("Resolved spring.flyway.password: {}", environment.getProperty("spring.flyway.password"));
        
        log.info("=== End Environment Properties Early Debug ===");
    }
}
