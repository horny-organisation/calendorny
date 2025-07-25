package ru.calendorny.taskservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;

public interface TaskManagerService {

    TaskResponse createTask(UUID userId, String title, String description, LocalDate dueDate, RruleDto rruleDto);

    List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate);

    TaskResponse getTask(UUID taskId);

    TaskResponse updateTask(UUID taskId, UUID userId, String title, String description, LocalDate dueDate,
                            TaskStatus status, RruleDto rruleDto);

    void deleteTask(UUID taskId);

    TaskResponse updateStatus(UUID taskId, TaskStatus status);
}
