package com.infobank.multiagentplatform.orchestrator.exception;

import com.infobank.multiagentplatform.commons.api.ApiError;
import com.infobank.multiagentplatform.commons.api.exception.CommonExceptions;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice(basePackages = "com.infobank.multiagentplatform.orchestrator")
public class OrchestratorExceptionHandler {

    @ExceptionHandler(CommonExceptions.ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CommonExceptions.ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.NOT_FOUND, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PlanParsingException.class)
    public ResponseEntity<ApiError> handlePlanParsing(PlanParsingException ex, HttpServletRequest request) {
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CommonExceptions.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(CommonExceptions.AccessDeniedException ex, HttpServletRequest request) {
        ApiError error = buildApiError(ex.getMessage(), HttpStatus.FORBIDDEN, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private ApiError buildApiError(String message, HttpStatus status, HttpServletRequest request) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .traceId(MDC.get("traceId"))
                .fieldErrors(Collections.emptyList())
                .build();
    }
}
