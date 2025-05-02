package com.infobank.multiagentplatform.orchestrator.model;

import com.infobank.multiagentplatform.orchestrator.model.ExecutionPlan;
import com.infobank.multiagentplatform.orchestrator.model.ExecutionResult;
import com.infobank.multiagentplatform.orchestrator.model.TaskBlock;
import com.infobank.multiagentplatform.domain.agent.task.AgentResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 반환용 DTO: 사용자에게 렌더링할 최종 결과(summary)와 로그용 상세 리스트(tasks) 모두 제공
 */
public class ExecutionPlanResponse {

    private final String summary;          // 마지막 블록의 결과물을 하나의 문자열로
    private final List<TaskResponse> tasks; // 로그나 디버그용 상세
    private final int totalBlocks;
    private final int totalTasks;

    public ExecutionPlanResponse(String summary,
                                 List<TaskResponse> tasks,
                                 int totalBlocks,
                                 int totalTasks) {
        this.summary = summary;
        this.tasks = tasks;
        this.totalBlocks = totalBlocks;
        this.totalTasks = totalTasks;
    }

    public String getSummary() {
        return summary;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    /**
     * ExecutionPlan + ExecutionResult를 조합해 DTO 생성
     * summary는 마지막 블록에 해당하는 Task들의 결과를 합쳐 생성합니다.
     */
    public static ExecutionPlanResponse from(ExecutionPlan plan, ExecutionResult execResult) {
        // 전체 태스크 상세
        List<TaskResponse> allTasks = plan.getBlocks().stream()
                .flatMap(block -> block.getTasks().stream())
                .map(task -> toTaskResponse(task.getAgentId(), task.getGoal(), task.getInputFrom(), execResult))
                .collect(Collectors.toList());

        // 마지막 블록 결과만을 summary로
        List<TaskResponse> finalTasks;
        if (plan.getBlocks().isEmpty()) {
            finalTasks = Collections.emptyList();
        } else {
            TaskBlock lastBlock = plan.getBlocks().get(plan.getBlocks().size() - 1);
            finalTasks = lastBlock.getTasks().stream()
                    .map(task -> toTaskResponse(task.getAgentId(), task.getGoal(), task.getInputFrom(), execResult))
                    .collect(Collectors.toList());
        }

        String summaryText = finalTasks.stream()
                .map(TaskResponse::getResult)
                .collect(Collectors.joining("\n"));

        return new ExecutionPlanResponse(
                summaryText,
                allTasks,
                plan.getBlocks().size(),
                allTasks.size()
        );
    }

    private static TaskResponse toTaskResponse(String taskId,
                                               String goal,
                                               String inputFrom,
                                               ExecutionResult execResult) {
        AgentResult agentResult = execResult.getRawResults().get(taskId);
        String result = (agentResult != null ? agentResult.getParsedResult().toString() : "");
        return new TaskResponse(taskId, goal, inputFrom, result);
    }

    /**
     * 태스크별 UI 표시용 데이터 구조
     */
    public static class TaskResponse {
        private final String taskId;
        private final String goal;
        private final String inputFrom;
        private final String result;

        public TaskResponse(String taskId,
                            String goal,
                            String inputFrom,
                            String result) {
            this.taskId = taskId;
            this.goal = goal;
            this.inputFrom = inputFrom;
            this.result = result;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getGoal() {
            return goal;
        }

        public String getInputFrom() {
            return inputFrom;
        }

        public String getResult() {
            return result;
        }
    }
}
