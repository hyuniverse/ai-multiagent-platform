package com.infobank.multiagentplatform.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder(
            @Value("${orchestrator.http.connect-timeout}") Duration connectTimeout,
            @Value("${orchestrator.http.read-timeout}")    Duration readTimeout) {

        // Reactor Netty HttpClient 구성
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                );

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        ExchangeFilterFunction mdcFilter = ExchangeFilterFunction.ofRequestProcessor(req -> {
            String traceId = MDC.get("traceId");
            ClientRequest filtered = ClientRequest.from(req)
                    .header("X-B3-TraceId", traceId != null ? traceId : "")
                    .build();
            return Mono.just(filtered);
        });

        return WebClient.builder()
                .clientConnector(connector)
                .filter(mdcFilter);
    }
}
