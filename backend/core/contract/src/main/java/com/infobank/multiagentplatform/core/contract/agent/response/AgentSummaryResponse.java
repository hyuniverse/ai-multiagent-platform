// AgentSummaryResponse.java
package com.infobank.multiagentplatform.core.contract.agent.response;

import com.infobank.multiagentplatform.domain.agent.model.AgentMetadata;
import lombok.*;

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

    @Builder
    private AgentSummaryResponse(String uuid, String name, String description, List<String> inputTypes, List<String> outputTypes) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
    }

    public static AgentSummaryResponse of(String uuid, AgentMetadata metadata) {
        return AgentSummaryResponse.builder()
                .uuid(uuid)
                .name(metadata.getName())
                .description(metadata.getDescription())
                .inputTypes(metadata.getInputTypes())
                .outputTypes(metadata.getOutputTypes())
                .build();
    }


}
