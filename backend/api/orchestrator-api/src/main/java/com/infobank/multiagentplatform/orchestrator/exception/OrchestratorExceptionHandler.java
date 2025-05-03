package com.infobank.multiagentplatform.orchestrator.exception;

import com.infobank.multiagentplatform.commons.api.dto.ApiError;
import com.infobank.multiagentplatform.commons.api.exception.CommonExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 오케스트레이터 전용 예외 처리 핸들러
 */
@RestControllerAdvice(basePackages = "com.infobank.multiagentplatform.orchestrator")
public class OrchestratorExceptionHandler {

    @ExceptionHandler(CommonExceptions.ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CommonExceptions.ResourceNotFoundException ex) {
        ApiError error = new ApiError("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PlanParsingException.class)
    public ResponseEntity<ApiError> handlePlanParsing(PlanParsingException ex) {
        ApiError error = new ApiError("INVALID_PLAN", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CommonExceptions.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(CommonExceptions.AccessDeniedException ex) {
        ApiError error = new ApiError("ACCESS_DENIED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
