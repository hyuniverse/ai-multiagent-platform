package com.infobank.multiagentplatform.commons.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Component
public class ReactiveMetricOperator {

    private final MeterRegistry meterRegistry;
    // Timer 객체는 비용이 있으므로, 한 번 생성하면 캐싱하여 재사용
    private final Map<String, Timer> timerCache = new ConcurrentHashMap<>();

    public ReactiveMetricOperator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Mono의 실행 시간을 측정하는 Function을 반환합니다.
     * 이 Function은 .transform() 연산자에서 사용됩니다.
     *
     * @param metricName 프로메테우스에 기록될 메트릭 이름
     * @param <T> Mono가 반환하는 타입
     * @return Mono를 입력받아 측정 로직이 추가된 Mono를 반환하는 Function
     */
    public <T> Function<Mono<T>, Mono<T>> measure(String metricName) {
        // 캐시에서 Timer를 찾거나, 없으면 새로 생성하여 캐시에 저장합니다.
        Timer timer = timerCache.computeIfAbsent(metricName,
                key -> Timer.builder(key).register(meterRegistry));

        return mono -> {
            final AtomicReference<Timer.Sample> sample = new AtomicReference<>();
            return mono
                    .doOnSubscribe(subscription ->
                            sample.set(Timer.start(meterRegistry))
                    )
                    .doFinally(signalType -> {
                        Timer.Sample s = sample.get();
                        if (s != null) {
                            s.stop(timer);
                        }
                    });
        };
    }
}