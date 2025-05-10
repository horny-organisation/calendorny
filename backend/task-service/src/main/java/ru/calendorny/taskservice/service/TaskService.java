package ru.calendorny.taskservice.service;

import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RruleDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse createNewTask (UUID userId, String title, String description, LocalDate dueDate, RruleDto rrule);

    List<TaskResponse> getTasksListByUserIdInTimeInterval(UUID userId, LocalDate from, LocalDate to);

    TaskResponse getTaskDetailsByTaskId(UUID taskId);

    TaskResponse updateTaskById(UUID taskId, String title, String description, LocalDate dueDate, TaskStatus status, RruleDto rrule);

    void deleteTaskById(UUID taskId);

    TaskResponse updateTaskStatusByTaskId(UUID taskId, TaskStatus newStatus);
}
