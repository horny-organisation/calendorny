package ru.calendorny.taskservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;

public interface TaskProcessor {

    boolean supports(UUID taskId);

    boolean supportsRecurTask(boolean isRecur);

    TaskResponse getTask(UUID taskId);

    TaskResponse createTask(UUID userId, String title, String desc, LocalDate date, RruleDto rruleDto);

    TaskResponse updateTask(
            UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto);

    void deleteTask(UUID taskId);

    TaskResponse updateStatus(UUID taskId, TaskStatus status);

    List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate);

    List<TaskResponse> getPendingTasksByDate(LocalDate date);

    void hardDeleteTask(UUID taskId);
}
