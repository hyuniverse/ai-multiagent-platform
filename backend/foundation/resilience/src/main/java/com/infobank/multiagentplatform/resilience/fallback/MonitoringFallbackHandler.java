package com.infobank.multiagentplatform.resilience.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MonitoringFallbackHandler implements FallbackHandler<Void> {

    private static final Logger log = LoggerFactory.getLogger(MonitoringFallbackHandler.class);
    // 간단한 로컬 버퍼 예시
    private final ConcurrentLinkedQueue<Runnable> buffer = new ConcurrentLinkedQueue<>();

    @Override
    public Void handle(Throwable ex, Object... args) {
        log.warn("Metrics push failed, buffering locally", ex);
        // args[0]에 메트릭 전송 작업(Runnable)을 전달받는다고 가정
        if (args.length > 0 && args[0] instanceof Runnable) {
            buffer.add((Runnable) args[0]);
        }
        return null;
    }

    /** 버퍼된 작업 재시도 */
    public void retryBuffered() {
        Runnable task;
        while ((task = buffer.poll()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Buffered metric retry failed", e);
                buffer.add(task);
                break;
            }
        }
    }
}
