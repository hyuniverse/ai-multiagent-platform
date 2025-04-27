package com.infobank.multiagentplatform.invoker.domain;

public class AgentResult {
    private String rawResponse;
    private Object parsedResult;

    public AgentResult() {}

    public AgentResult(String rawResponse, Object parsedResult) {
        this.rawResponse = rawResponse;
        this.parsedResult = parsedResult;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public Object getParsedResult() {
        return parsedResult;
    }

    public void setParsedResult(Object parsedResult) {
        this.parsedResult = parsedResult;
    }
}
