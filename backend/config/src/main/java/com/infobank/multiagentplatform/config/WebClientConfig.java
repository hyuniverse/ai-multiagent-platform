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
    public Scheduler boundedElasticScheduler(
            @Value("${reactor.scheduler.bounded-elastic.max-threads:#{T(java.lang.Runtime).getRuntime().availableProcessors()*2}}") int maxThreads,
            @Value("${reactor.scheduler.bounded-elastic.queue-capacity:10000}") int queueCapacity,
            @Value("${reactor.scheduler.bounded-elastic.thread-name:json-parsing}") String threadName,
            @Value("${reactor.scheduler.bounded-elastic.ttl-seconds:60}") int ttlSeconds,
            @Value("${reactor.scheduler.bounded-elastic.daemon:true}") boolean daemon
    ) {
        log.info("[Scheduler] boundedElastic config => maxThreads={}, queueCapacity={}, ttlSeconds={}, threadName={}, daemon={}",
                maxThreads, queueCapacity, ttlSeconds, threadName, daemon);
        return Schedulers.newBoundedElastic(
                maxThreads,
                queueCapacity,
                threadName,
                ttlSeconds,
                daemon
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

    // 공용 풀 (프로퍼티화, 기존 동작 기본값 유지)
    @Bean
    @Primary
    public ConnectionProvider connectionProvider(
            @Value("${webclient.default.pool.name:custom}") String poolName,
            @Value("${webclient.default.pool.max-connections:500}") int maxConnections,
            @Value("${webclient.default.pool.max-idle-time:20s}") Duration maxIdleTime,
            @Value("${webclient.default.pool.max-life-time:60s}") Duration maxLifeTime,
            @Value("${webclient.default.pool.pending-acquire-timeout:5s}") Duration pendingAcquireTimeout
    ) {
        log.info("[ConnectionPool][default] config => name={}, maxConnections={}, maxIdleTime={}, maxLifeTime={}, pendingAcquireTimeout={}",
                poolName, maxConnections, maxIdleTime, maxLifeTime, pendingAcquireTimeout);
        return ConnectionProvider.builder(poolName)
                .maxConnections(maxConnections)
                .maxIdleTime(maxIdleTime)
                .maxLifeTime(maxLifeTime)
                .pendingAcquireTimeout(pendingAcquireTimeout)
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build()
                ;
    }

    // LLM 전용 커넥션 풀 (프로퍼티화)
    @Bean
    @Qualifier("llmConnectionProvider")
    public ConnectionProvider llmConnectionProvider(
            @Value("${webclient.llm.pool.name:llm-pool}") String poolName,
            @Value("${webclient.llm.pool.max-connections:200}") int maxConnections,
            @Value("${webclient.llm.pool.max-idle-time:30s}") Duration maxIdleTime,
            @Value("${webclient.llm.pool.pending-acquire-timeout:3s}") Duration pendingAcquireTimeout
    ) {
        log.info("[ConnectionPool][llm] config => name={}, maxConnections={}, maxIdleTime={}, pendingAcquireTimeout={}",
                poolName, maxConnections, maxIdleTime, pendingAcquireTimeout);
        return ConnectionProvider.builder(poolName)
                .maxConnections(maxConnections)
                .maxIdleTime(maxIdleTime)
                .pendingAcquireTimeout(pendingAcquireTimeout)
                .evictInBackground(Duration.ofSeconds(120))
                .metrics(true)
                .build();
    }

    // Agent 전용 커넥션 풀 (프로퍼티화)
    @Bean
    @Qualifier("agentConnectionProvider")
    public ConnectionProvider agentConnectionProvider(
            @Value("${webclient.agent.pool.name:agent-pool}") String poolName,
            @Value("${webclient.agent.pool.max-connections:200}") int maxConnections,
            @Value("${webclient.agent.pool.max-idle-time:30s}") Duration maxIdleTime,
            @Value("${webclient.agent.pool.pending-acquire-timeout:3s}") Duration pendingAcquireTimeout
    ) {
        log.info("[ConnectionPool][agent] config => name={}, maxConnections={}, maxIdleTime={}, pendingAcquireTimeout={}",
                poolName, maxConnections, maxIdleTime, pendingAcquireTimeout);
        return ConnectionProvider.builder(poolName)
                .maxConnections(maxConnections)
                .maxIdleTime(maxIdleTime)
                .pendingAcquireTimeout(pendingAcquireTimeout)
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

    private HttpClient httpClient(ConnectionProvider provider, Duration connectTimeout, Duration readTimeout, Duration responseTimeout) {
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .responseTimeout(responseTimeout)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeout.toMillis(), TimeUnit.MILLISECONDS))
                )
                .metrics(true, s -> s);
    }

    // 기본 WebClient.Builder (프로퍼티화 및 하위 호환)
    @Bean
    @Primary
    public WebClient.Builder webClientBuilder(
            @Value("${webclient.default.connect-timeout}") Duration connectTimeout,
            @Value("${webclient.default.read-timeout}")    Duration readTimeout,
            @Value("${webclient.default.response-timeout:2500ms}") Duration responseTimeout,
            ObjectMapper objectMapper,
            ConnectionProvider connectionProvider) {

        log.info("[WebClient][default] timeouts => connect={}, read={}, response={}", connectTimeout, readTimeout, responseTimeout);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout, responseTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }

    // LLM 전용 WebClient.Builder (프로퍼티화 및 하위 호환)
    @Bean
    @Qualifier("llmWebClientBuilder")
    public WebClient.Builder llmWebClientBuilder(
            @Value("${webclient.llm.connect-timeout}") Duration connectTimeout,
            @Value("${webclient.llm.read-timeout}")    Duration readTimeout,
            @Value("${webclient.llm.response-timeout:${webclient.default.response-timeout:2500ms}}") Duration responseTimeout,
            ObjectMapper objectMapper,
            @Qualifier("llmConnectionProvider") ConnectionProvider connectionProvider) {

        log.info("[WebClient][llm] timeouts => connect={}, read={}, response={}", connectTimeout, readTimeout, responseTimeout);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout, responseTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }

    // Agent 전용 WebClient.Builder (프로퍼티화 및 하위 호환)
    @Bean
    @Qualifier("agentWebClientBuilder")
    public WebClient.Builder agentWebClientBuilder(
        @Value("${webclient.agent.connect-timeout}") Duration connectTimeout,
        @Value("${webclient.agent.read-timeout}")    Duration readTimeout,
        @Value("${webclient.agent.response-timeout:${webclient.default.response-timeout:2500ms}}") Duration responseTimeout,
        ObjectMapper objectMapper,
        @Qualifier("agentConnectionProvider") ConnectionProvider connectionProvider) {

        log.info("[WebClient][agent] timeouts => connect={}, read={}, response={}", connectTimeout, readTimeout, responseTimeout);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                httpClient(connectionProvider, connectTimeout, readTimeout, responseTimeout)
        );

        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(exchangeStrategies(objectMapper))
                .filter(mdcFilter());
    }
}
