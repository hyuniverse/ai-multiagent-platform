package com.infobank.multiagentplatform.resilience.annotation;

import com.infobank.multiagentplatform.resilience.fallback.FallbackHandler;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Fallback {
    /** FallbackHandler 구현체 */
    Class<? extends FallbackHandler<?>> handler();
}
