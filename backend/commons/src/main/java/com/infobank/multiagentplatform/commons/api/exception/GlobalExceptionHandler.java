package com.infobank.multiagentplatform.commons.api.exception;

import com.infobank.multiagentplatform.commons.api.ApiError;
import com.infobank.multiagentplatform.commons.api.ApiError.FieldError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            WebExchangeBindException ex,
            ServerWebExchange exchange
    ) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .path(exchange.getRequest().getURI().getPath())
                .traceId(MDC.get("traceId"))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            ServerWebExchange exchange
    ) {
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(exchange.getRequest().getURI().getPath())
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    // Propagate downstream HTTP status from WebClient (e.g., 429 Too Many Requests)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWebClientResponse(
            WebClientResponseException ex,
            ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.error("[DownstreamError] 경로: {} {}", method, path);
        log.error("[DownstreamError] 상태: {} {}", ex.getStatusCode().value(), ex.getStatusText());
        log.error("[DownstreamError] 메시지: {}", ex.getMessage());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatusCode().value())
                .message(ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS
                        ? "요청이 너무 많습니다. 잠시 후 다시 시도하세요."
                        : ex.getStatusText())
                .path(path)
                .traceId(MDC.get("traceId"))
                .build();

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(ex.getStatusCode());
        String retryAfter = ex.getHeaders().getFirst("Retry-After");
        if (retryAfter != null && !retryAfter.isBlank()) {
            builder.header("Retry-After", retryAfter);
        }
        return builder.body(error);
    }

    // Reactor/operator timeout -> 504 Gateway Timeout
    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public ResponseEntity<ApiError> handleTimeout(
            java.util.concurrent.TimeoutException ex,
            ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getURI().getPath();
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.GATEWAY_TIMEOUT.value())
                .message("요청 처리 시간이 초과되었습니다.")
                .path(path)
                .traceId(MDC.get("traceId"))
                .build();
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error);
    }

    // Handle IllegalStateException that wraps downstream errors (e.g., from fallback methods)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            ServerWebExchange exchange
    ) {
        Throwable cause = ex.getCause();
        if (cause instanceof WebClientResponseException wcre) {
            return handleWebClientResponse(wcre, exchange);
        }
        if (cause instanceof java.util.concurrent.TimeoutException te) {
            return handleTimeout(te, exchange);
        }
        return handleAllExceptions(ex, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(
            Exception ex,
            ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        // Map circuit-open without adding resilience4j dependency
        Throwable cause = ex.getCause();
        if (isCircuitOpen(ex) || isCircuitOpen(cause)) {
            ApiError error = ApiError.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .message("일시적으로 서비스를 사용할 수 없습니다. (circuit open)")
                    .path(path)
                    .traceId(MDC.get("traceId"))
                    .build();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }

        log.error("[InternalError] 경로: {} {}", method, path);
        log.error("[InternalError] 예외 타입: {}", ex.getClass().getSimpleName());
        log.error("[InternalError] 에러 메시지: {}", ex.getMessage());
        log.error("[InternalError] 스택 트레이스:", ex);

        if (cause != null) {
            log.error("[InternalError] 원인 예외: {} - {}", cause.getClass().getSimpleName(), cause.getMessage());
        }

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("서버 내부 오류가 발생했습니다.")
                .path(path)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private boolean isCircuitOpen(Throwable t) {
        if (t == null) return false;
        String cn = t.getClass().getName();
        return "io.github.resilience4j.circuitbreaker.CallNotPermittedException".equals(cn);
    }
}