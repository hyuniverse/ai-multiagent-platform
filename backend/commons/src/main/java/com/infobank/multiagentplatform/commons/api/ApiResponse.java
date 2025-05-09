package com.infobank.multiagentplatform.commons.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final int code;
    private final String status;
    private final String message;
    private final T data;

    @JsonCreator
    public ApiResponse(
            @JsonProperty("code") int code,
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Factory 메서드
    public static <T> ApiResponse<T> of(int code, String status, String message, T data) {
        return new ApiResponse<>(code, status, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "OK", "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "CREATED", "리소스가 성공적으로 생성되었습니다.", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, "ERROR", message, null);
    }
}
