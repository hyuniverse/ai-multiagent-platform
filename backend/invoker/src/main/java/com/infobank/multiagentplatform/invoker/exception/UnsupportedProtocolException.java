package com.infobank.multiagentplatform.invoker.exception;

public class UnsupportedProtocolException extends RuntimeException {
    public UnsupportedProtocolException(String protocol) {
        super("지원하지 않는 프로토콜입니다: " + protocol);
    }
}
