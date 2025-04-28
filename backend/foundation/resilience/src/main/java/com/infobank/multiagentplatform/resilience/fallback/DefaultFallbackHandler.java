package com.infobank.multiagentplatform.resilience.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultFallbackHandler implements FallbackHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(DefaultFallbackHandler.class);

    @Override
    public Object handle(Throwable ex, Object... args) {
        log.error("DefaultFallbackHandler invoked, rethrowing exception", ex);
        // 기본은 예외를 래핑해서 다시 던집니다.
        throw new RuntimeException("Fallback 처리 중 예외 발생", ex);
        // 또는 return null;
    }
}
