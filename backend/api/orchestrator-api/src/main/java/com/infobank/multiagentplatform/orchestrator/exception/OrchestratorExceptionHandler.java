package com.infobank.multiagentplatform.orchestrator.exception;

import com.infobank.multiagentplatform.commons.api.ApiError;
import com.infobank.multiagentplatform.commons.api.exception.CommonExceptions;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange; // 변경된 부분

import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
public class OrchestratorExceptionHandler {

    @ExceptionHandler(CommonExceptions.ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CommonExceptions.ResourceNotFoundException ex, ServerWebExchange exchange) { // 변경된 부분
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.NOT_FOUND, exchange);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PlanParsingException.class)
    public ResponseEntity<ApiError> handlePlanParsing(PlanParsingException ex, ServerWebExchange exchange) { // 변경된 부분
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.BAD_REQUEST, exchange);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CommonExceptions.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(CommonExceptions.AccessDeniedException ex, ServerWebExchange exchange) { // 변경된 부분
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.FORBIDDEN, exchange);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private ApiError buildApiError(String message, HttpStatus status, ServerWebExchange exchange) { // 변경된 부분
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(exchange.getRequest().getURI().getPath()) // 변경된 부분
                .traceId(MDC.get("traceId"))
                .fieldErrors(Collections.emptyList())
                .build();
    }
}