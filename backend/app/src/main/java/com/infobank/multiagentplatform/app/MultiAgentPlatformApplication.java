package com.infobank.multiagentplatform.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.blockhound.BlockHound;

@SpringBootApplication(scanBasePackages = "com.infobank.multiagentplatform")
@EnableR2dbcRepositories(basePackages = "com.infobank.multiagentplatform.domain.agent.repository")
@EnableR2dbcAuditing
@EnableScheduling
public class MultiAgentPlatformApplication {

    public static void main(String[] args) {
        BlockHound.builder()
            .allowBlockingCallsInside("io.swagger.v3.core.util.ReflectionUtils", "loadClass")
            .allowBlockingCallsInside("org.springdoc.core.utils.SpringDocUtils", "getField")
            .install();
        SpringApplication.run(MultiAgentPlatformApplication.class, args);
    }

}

