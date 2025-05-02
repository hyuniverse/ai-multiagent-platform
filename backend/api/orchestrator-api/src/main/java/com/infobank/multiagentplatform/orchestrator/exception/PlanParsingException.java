package com.infobank.multiagentplatform.orchestrator.exception;

public class PlanParsingException extends RuntimeException{
    public PlanParsingException(String message) { super(message); };
    public PlanParsingException(String message, Throwable cause) { super(message, cause); };
    public PlanParsingException(Throwable cause) { super(cause); };
    public PlanParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    };
}
