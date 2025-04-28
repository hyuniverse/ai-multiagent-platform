package com.infobank.multiagentplatform.resilience.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {
    /** ResilienceConfig에 정의된 RetryConfig 빈 이름 */
    String name() default "defaultRetry";

    /** 재시도 후에도 실패 시 호출할 메서드 */
    String fallbackMethod() default "";

    /** 재시도 트리거 예외 타입 */
    Class<? extends Throwable>[] retryExceptions() default { Exception.class };
}
