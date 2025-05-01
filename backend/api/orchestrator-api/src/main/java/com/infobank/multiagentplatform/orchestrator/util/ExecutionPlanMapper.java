package com.infobank.multiagentplatform.orchestrator.util;

import com.infobank.multiagentplatform.domain.agent.task.AgentTask;
import com.infobank.multiagentplatform.orchestrator.
import com.infobank.multiagentplatform.orchestrator.application.dto.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.application.dto.TaskDto;
import com.infobank.multiagentplatform.orchestrator.domain.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.domain.TaskBlock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ExecutionPlanMapper {

    @Mapping(target = "blocks", source = "blocks")
    @Mapping(target = "unassigned", source = "unassigned")
    ExecutionPlan toDomain(ExecutionPlan dto);

    default List<TaskBlock> map(List<BlockDto> blocks) {
        if (blocks == null) return Collections.emptyList();
        return blocks.stream()
                .map(b -> new TaskBlock(
                        b.getTasks().stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    default AgentTask toDomain(TaskDto t) {
        return AgentTask.builder()
                .goal(t.getGoal())
                .agentId(t.getAgentId())
                .inputType(t.getInputType())
                .inputFrom(t.getInputFrom())
                .build();
    }

    default List<AgentTask> mapUnassigned(List<String> unassigned) {
        if (unassigned == null) return Collections.emptyList();
        return unassigned.stream()
                .map(goal -> AgentTask.builder()
                        .goal(goal)
                        .agentId(null)
                        .inputType(null)
                        .inputFrom(null)
                        .build())
                .collect(Collectors.toList());
    }
}
