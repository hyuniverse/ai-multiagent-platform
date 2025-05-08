package com.infobank.multiagentplatform.commons.api;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
    private String traceId;

    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String message;
    }
}
