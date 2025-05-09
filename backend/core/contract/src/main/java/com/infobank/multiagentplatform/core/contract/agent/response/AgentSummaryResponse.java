package com.infobank.multiagentplatform.core.contract.agent.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import lombok.Getter;

import java.util.List;

/**
 * GET /agents/summaries 응답용 (경량 정보)
 */
@Getter
public class AgentSummaryResponse {
    private final String uuid;
    private final String name;
    private final String description;
    private final List<String> inputTypes;
    private final List<String> outputTypes;

    @JsonCreator
    public AgentSummaryResponse(
            @JsonProperty("uuid") String uuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("inputTypes") List<String> inputTypes,
            @JsonProperty("outputTypes") List<String> outputTypes
    ) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
    }

    public static AgentSummaryResponse of(String uuid, AgentMetadata metadata) {
        return new AgentSummaryResponse(
                uuid,
                metadata.getName(),
                metadata.getDescription(),
                metadata.getInputTypes(),
                metadata.getOutputTypes()
        );
    }
}
