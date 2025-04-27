package com.infobank.multiagentplatform.orchestrator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.client")
public class LLMClientProperties {
    private String apiKey;
    private String apiUrl;
    private String model;
}
