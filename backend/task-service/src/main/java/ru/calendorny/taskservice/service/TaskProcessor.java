package ru.calendorny.taskservice.service;

import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RruleDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskProcessor {

    boolean supports(UUID taskId);
    TaskResponse getTask(UUID taskId);
    TaskResponse updateTask(UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto);
    void deleteTask(UUID taskId);
    TaskResponse updateStatus(UUID taskId, TaskStatus status);

    void hardDeleteTask(UUID taskId);

    boolean supportsRecurringTask(boolean recurring);

    TaskResponse createTask(UUID userId, String title, String desc, LocalDate date, RruleDto rruleDto);

    List<TaskResponse> getTasksByUserIdAndDateRange(UUID userId, LocalDate fromDate, LocalDate toDate);
}
