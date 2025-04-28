package com.infobank.multiagentplatform.resilience.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CircuitBreaker {
    /** ResilienceConfig에 정의된 CircuitBreakerConfig 빈 이름 */
    String name() default "defaultCircuitBreaker";

    /** Circuit 열림(open) 상태에서 호출할 메서드 */
    String fallbackMethod() default "";
}
