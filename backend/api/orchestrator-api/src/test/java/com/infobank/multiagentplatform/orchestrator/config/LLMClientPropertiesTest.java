package com.infobank.multiagentplatform.orchestrator.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LLMClientPropertiesTest {

    private LLMClientProperties properties;

    @Test
    void shouldLoadFromApplicationYml() {
        assertThat(properties.getApiKey()).isEqualTo("실제 application.yml 값");
        assertThat(properties.getApiUrl()).isEqualTo("https://api.openai.com/...");
        assertThat(properties.getModel()).isEqualTo("gpt-4");

    }

}