package com.infobank.multiagentplatform.resilience.aspect;

import com.infobank.multiagentplatform.resilience.annotation.CircuitBreaker;
import com.infobank.multiagentplatform.resilience.annotation.Fallback;
import com.infobank.multiagentplatform.resilience.annotation.Retryable;
import com.infobank.multiagentplatform.resilience.fallback.FallbackHandler;
import com.infobank.multiagentplatform.resilience.registry.ResilienceRegistry;

import io.github.resilience4j.retry.Retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.Supplier;

@Aspect
@Component
public class ResilienceAspect {


    private final ResilienceRegistry resilienceRegistry;
    private final ApplicationContext applicationContext;

    public ResilienceAspect(ResilienceRegistry resilienceRegistry,
                            ApplicationContext applicationContext) {
        this.resilienceRegistry = resilienceRegistry;
        this.applicationContext = applicationContext;
    }

    @Around(
            "@annotation(com.infobank.multiagentplatform.resilience.annotation.Retryable) || " +
                    "@annotation(com.infobank.multiagentplatform.resilience.annotation.CircuitBreaker)"
    )
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();

        // 애노테이션 정보 꺼내기
        Retryable retryableAnno = method.getAnnotation(Retryable.class);
        com.infobank.multiagentplatform.resilience.annotation.CircuitBreaker cbAnno = method.getAnnotation(CircuitBreaker.class);

        // 실제 호출을 래핑할 Supplier
        Supplier<Object> supplier = () -> {
            try {
                return pjp.proceed();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };

        // 1) Retry 적용
        if (retryableAnno != null) {
            Retry retry = resilienceRegistry
                    .getRetryRegistry()
                    .retry(retryableAnno.name());
            supplier = Retry.decorateSupplier(retry, supplier);
        }

        // 2) CircuitBreaker 적용
        if (cbAnno != null) {
            // full qualified name 사용
            io.github.resilience4j.circuitbreaker.CircuitBreaker r4jCb =
                    resilienceRegistry.getCircuitBreakerRegistry()
                            .circuitBreaker(cbAnno.name());
            supplier = io.github.resilience4j.circuitbreaker.CircuitBreaker
                    .decorateSupplier(r4jCb, supplier);
        }

        // 3) 실행 및 Fallback 처리
        try {
            return supplier.get();
        } catch (Throwable wrapperEx) {
            // 1) 래퍼가 있으면 언랩, 없으면 그대로
            Throwable realEx = (wrapperEx.getCause() != null)
                    ? wrapperEx.getCause()
                    : wrapperEx;

            // 3-1) fallbackMethod() 호출 우선
            String fallbackMethod = "";
            if (retryableAnno != null && !retryableAnno.fallbackMethod().isEmpty()) {
                fallbackMethod = retryableAnno.fallbackMethod();
            } else if (cbAnno != null && !cbAnno.fallbackMethod().isEmpty()) {
                fallbackMethod = cbAnno.fallbackMethod();
            }

            if (!fallbackMethod.isEmpty()) {
                return invokeFallbackMethod(
                        pjp.getTarget(),
                        fallbackMethod,
                        realEx,         // ← 여기서 realEx 사용
                        pjp.getArgs()
                );
            }

            // 3-2) @Fallback(handler=...) 처리
            Fallback fb = method.getAnnotation(Fallback.class);
            if (fb != null) {
                FallbackHandler<?> handler =
                        applicationContext.getBean(fb.handler());
                return handler.handle(realEx, pjp.getArgs());
            }

            // 3-3) 모두 해당되지 않으면 예외 재던짐
            throw realEx;
        }
    }

    /**
     * 리플렉션으로 fallbackMethod 호출
     */
    private Object invokeFallbackMethod(Object target,
                                        String methodName,
                                        Throwable ex,
                                        Object[] args) throws Throwable {
        for (Method m : target.getClass().getMethods()) {
            if (!m.getName().equals(methodName)) continue;
            Class<?>[] pts = m.getParameterTypes();
            // 첫번째 파라미터가 Throwable인지, 나머지는 args 길이와 같은지 검증
            if (pts.length == args.length + 1
                    && Throwable.class.isAssignableFrom(pts[0])) {
                m.setAccessible(true);
                Object[] callArgs = new Object[args.length + 1];
                callArgs[0] = ex;
                System.arraycopy(args, 0, callArgs, 1, args.length);
                return m.invoke(target, callArgs);
            }
        }
        throw new NoSuchMethodException("Fallback method '" + methodName +
                "' not found on " + target.getClass());
    }
}
