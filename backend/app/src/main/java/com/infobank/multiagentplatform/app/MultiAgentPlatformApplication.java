package com.infobank.multiagentplatform.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.infobank.multiagentplatform")
@EnableJpaRepositories(basePackages = "com.infobank.multiagentplatform.domain.agent.repository")
@EntityScan(basePackages = "com.infobank.multiagentplatform.domain.agent.entity")
@ConfigurationPropertiesScan("com.infobank.multiagentplatform.resilience.config")
@EnableJpaAuditing
@EnableScheduling
public class MultiAgentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiAgentPlatformApplication.class, args);
    }

}

