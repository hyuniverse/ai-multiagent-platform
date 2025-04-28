package com.infobank.multiagentplatform.resilience.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigReloadFallbackHandler implements FallbackHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(ConfigReloadFallbackHandler.class);

    @Override
    public Object handle(Throwable ex, Object... args) {
        log.warn("Config reload failed, using previous config", ex);
        // args[0]에 이전 Config 객체가 전달된다고 가정
        if (args != null && args.length > 0) {
            return args[0];
        }
        return null;
    }
}
