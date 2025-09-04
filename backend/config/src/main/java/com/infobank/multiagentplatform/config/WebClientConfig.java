package com.infobank.multiagentplatform.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Bean
    @Qualifier("boundedElasticScheduler")
    public Scheduler boundedElasticScheduler() {
        return Schedulers.newBoundedElastic(
                Runtime.getRuntime().availableProcessors() * 2,
                Integer.MAX_VALUE, // 큐 크기
                "json-parsing", // 스레드 이름
                60, // TTL
                true // daemon
        );
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                // 성능 최적화 설정
                .disable(SerializationFeature.INDENT_OUTPUT) // 들여쓰기 비활성화
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                // 추가 Jackson 성능 최적화
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES) // null 값 맵 쓰기 비활성화
                .disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS) // 빈 배열 쓰기 비활성화
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS); // BigDecimal 사용으로 정밀도 향상
        
        warmupJacksonCache(mapper);
        
        return mapper;
    }
    
    private void warmupJacksonCache(ObjectMapper mapper) {
        try {
            // 주요 응답 타입들을 미리 캐싱 (의존성이 있는 타입들만)
            mapper.canDeserialize(mapper.constructType(String.class));
            mapper.canDeserialize(mapper.constructType(java.util.Map.class));
            mapper.canDeserialize(mapper.constructType(java.util.List.class));
            mapper.canDeserialize(mapper.constructType(com.fasterxml.jackson.databind.JsonNode.class));
            
            // commons 모듈의 타입만 캐싱 (config 모듈이 의존성을 가진 타입)
            mapper.canDeserialize(mapper.constructType(com.infobank.multiagentplatform.commons.api.ApiResponse.class));
            
            // 자주 사용되는 원시 타입들 캐싱
            mapper.canDeserialize(mapper.constructType(Integer.class));
            mapper.canDeserialize(mapper.constructType(Boolean.class));
            mapper.canDeserialize(mapper.constructType(java.time.LocalDateTime.class));
            
            log.info("Jackson 캐시 워밍업 완료");
        } catch (Exception e) {
            log.warn("Jackson 캐시 워밍업 실패: {}", e.getMessage());
        }
    }

    @Bean
    @Primary
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build();
    }

    @Bean
    @Qualifier("llmConnectionProvider")
    public ConnectionProvider llmConnectionProvider() {
        return ConnectionProvider.builder("llm-pool")
                .maxConnections(200)
                .maxIdleTime(Duration.ofSeconds(30))
                .pendingAcquireTimeout(Duration.ofSeconds(3))
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build();
    }

    @Bean
    @Qualifier("agentConnectionProvider")
    public ConnectionProvider agentConnectionProvider() {
        return ConnectionProvider.builder("agent-pool")
                .maxConnections(200)
                .maxIdleTime(Duration.ofSeconds(30))
                .pendingAcquireTimeout(Duration.ofSeconds(3))
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build();
    }

    private ExchangeStrategies exchangeStrategies(ObjectMapper objectMapper) {
        return ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10MB
                })
                .build();
    }

    private ExchangeFilterFunction mdcFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            String traceId = MDC.get("traceId");
            ClientRequest filtered = ClientRequest.from(req)
                    .header("X-B3-TraceId", traceId != null ? traceId : "")
                    .build();
            return Mono.just(filtered);
        });
    }

    private HttpClient httpClient(ConnectionProvider provider, Duration connectTimeout, Duration readTimeout) {
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                )
                .metrics(true, s -> s);
    }

    @Bean
    @Primary
    public WebClient.Builder webClientBuilder(
            @Value("${orchestrator.http.connect-timeout}") Duration connectTimeout,
            @Value("${orchestrator.http.read-timeout}")    Duration readTimeout,
            ObjectMapper objectMapper,
            ConnectionProvider connectionProvider) {

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }

    @Bean
    @Qualifier("llmWebClientBuilder")
    public WebClient.Builder llmWebClientBuilder(
            @Value("${llm.client.connect-timeout}") Duration connectTimeout,
            @Value("${llm.client.read-timeout}")    Duration readTimeout,
            ObjectMapper objectMapper,
            @Qualifier("llmConnectionProvider") ConnectionProvider connectionProvider) {

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }

    @Bean
    @Qualifier("agentWebClientBuilder")
    public WebClient.Builder agentWebClientBuilder(
        @Value("${agent.client.connect-timeout}") Duration connectTimeout,
        @Value("${agent.client.read-timeout}")    Duration readTimeout,
        ObjectMapper objectMapper,
        @Qualifier("agentConnectionProvider") ConnectionProvider connectionProvider) {

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }
}
