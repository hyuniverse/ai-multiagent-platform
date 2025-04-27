package com.infobank.multiagentplatform.invoker.domain;

import lombok.Getter;

@Getter
public class AgentRequest {
    private String userInput;
    private Object input;

    public AgentRequest() {}

    public AgentRequest(String userInput) {
        this.userInput = userInput;
        this.input = null;
    }

    public AgentRequest(String userInput, Object input) {
        this.userInput = userInput;
        this.input = input;
    }
}
