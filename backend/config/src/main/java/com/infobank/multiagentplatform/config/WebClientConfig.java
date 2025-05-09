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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(
            WebClient.Builder builder,
            @Value("${orchestrator.http.connect-timeout}") Duration connectTimeout,
            @Value("${orchestrator.http.read-timeout}")    Duration readTimeout) {

        // 1) Reactor Netty HttpClient 구성: 커넥트 & 리드/라이트 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                );

        // 2) WebClient 에 ReactorClientHttpConnector 로 연결
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // 3) 기존에 쓰시던 MDC 필터까지 그대로 유지
                .filter((request, next) -> {
                    String traceId = MDC.get("traceId");
                    ClientRequest filtered = ClientRequest.from(request)
                            .header("X-B3-TraceId", traceId != null ? traceId : "")
                            .build();
                    return next.exchange(filtered);
                })
                .build();
    }
}
