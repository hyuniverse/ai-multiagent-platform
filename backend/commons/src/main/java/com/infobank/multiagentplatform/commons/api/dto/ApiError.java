package com.infobank.multiagentplatform.commons.api.dto;

/**
 * 일관된 에러 응답 형식을 나타내는 DTO
 */
public class ApiError {
    private String code;
    private String message;

    public ApiError() {
    }

    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
