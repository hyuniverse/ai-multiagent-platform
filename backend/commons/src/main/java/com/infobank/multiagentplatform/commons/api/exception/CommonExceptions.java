package com.infobank.multiagentplatform.commons.api.exception;

/**
 * API 공통 예외 타입 정의 (커먼 예외)
 */
public class CommonExceptions {
    private CommonExceptions() {
    }

    /**
     * 리소스를 찾을 수 없을 때 사용
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 권한이 없을 때 사용
     */
    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
