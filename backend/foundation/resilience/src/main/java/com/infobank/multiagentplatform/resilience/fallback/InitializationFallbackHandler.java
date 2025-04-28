package com.infobank.multiagentplatform.resilience.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InitializationFallbackHandler implements FallbackHandler<Void> {

    private static final Logger log = LoggerFactory.getLogger(InitializationFallbackHandler.class);

    @Override
    public Void handle(Throwable ex, Object... args) {
        log.error("Application initialization failed, shutting down", ex);
        // Context를 args[0]로 전달받아 종료할 수 있습니다.
        if (args.length > 0 && args[0] instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) args[0]).close();
        }
        return null;
    }
}
