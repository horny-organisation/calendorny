package ru.calendorny.taskservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.taskservice.api.TaskApi;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.security.AuthenticatedUser;
import ru.calendorny.taskservice.service.TaskManagerService;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskManagerService taskManagerService;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.createTask(
            userId, request.title(), request.description(), request.dueDate(), request.rrule());
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(LocalDate from, LocalDate to, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.getTasksByDateRange(userId, from, to);
    }

    @Override
    public TaskResponse getTask(UUID taskId, AuthenticatedUser authenticatedUser) {
        return taskManagerService.getTask(taskId);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.updateTask(
            taskId,
            userId,
            request.title(),
            request.description(),
            request.dueDate(),
            request.status(),
            request.rrule());
    }

    @Override
    public void deleteTask(UUID taskId, AuthenticatedUser authenticatedUser) {
        taskManagerService.deleteTask(taskId);
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, UpdateTaskStatusRequest request, AuthenticatedUser authenticatedUser) {
        return taskManagerService.updateStatus(taskId, request.status());
    }
}
