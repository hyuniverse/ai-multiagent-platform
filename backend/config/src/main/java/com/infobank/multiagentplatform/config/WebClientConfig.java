package com.infobank.multiagentplatform.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter((request, next) -> {
                    String traceId = MDC.get("traceId");
                    return next.exchange(
                            ClientRequest.from(request)
                                    .header("X-B3-TraceId", traceId != null ? traceId : "")
                                    .build()
                    );
                })
                .build();
    }
}
