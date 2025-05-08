package com.infobank.multiagentplatform.app;

import com.infobank.multiagentplatform.core.infra.broker.BrokerClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.ApplicationContext;


@SpringBootApplication(scanBasePackages = "com.infobank.multiagentplatform")
@EnableJpaRepositories(basePackages = "com.infobank.multiagentplatform.domain.agent.repository")
@EntityScan(basePackages = "com.infobank.multiagentplatform.domain.agent.entity")
@ConfigurationPropertiesScan("com.infobank.multiagentplatform.resilience.config")
@EnableJpaAuditing
public class MultiAgentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiAgentPlatformApplication.class, args);
    }

    @Bean
    public CommandLineRunner checkBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("=== Registered BrokerClient Beans ===");
            for (String name : ctx.getBeanNamesForType(BrokerClient.class)) {
                System.out.println(" -> " + name);
            }

            System.out.println("=== Registered RestTemplate Beans ===");
            for (String name : ctx.getBeanNamesForType(RestTemplate.class)) {
                System.out.println(" -> " + name);
            }
        };
    }
}

