package com.infobank.multiagentplatform.resilience.annotation;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Import({ ResilienceAspect.class })  // 나중에 작성할 Aspect 클래스
public @interface EnableResilience {
}
