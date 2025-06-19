package ru.calendorny.taskservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.securitystarter.AuthenticatedUser;
import ru.calendorny.taskservice.api.TaskApi;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.service.TaskManagerService;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskManagerService taskManagerService;

    @Override
    public TaskResponse createTask(CreateTaskRequest createTaskRequest, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.createTask(
            userId,
            createTaskRequest.title(),
            createTaskRequest.description(),
            createTaskRequest.dueDate(),
            createTaskRequest.rrule()
        );
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(LocalDate fromDate, LocalDate toDate, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.getTasksByDateRange(userId, fromDate, toDate);
    }

    @Override
    public TaskResponse getTask(UUID taskId, AuthenticatedUser authenticatedUser) {
        return taskManagerService.getTask(taskId);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest updateTaskRequest, AuthenticatedUser authenticatedUser) {
        UUID userId = authenticatedUser.id();
        return taskManagerService.updateTask(
            taskId,
            userId,
            updateTaskRequest.title(),
            updateTaskRequest.description(),
            updateTaskRequest.dueDate(),
            updateTaskRequest.status(),
            updateTaskRequest.rrule()
        );
    }

    @Override
    public void deleteTask(UUID taskId, AuthenticatedUser authenticatedUser) {
        taskManagerService.deleteTask(taskId);
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, UpdateTaskStatusRequest updateTaskStatusRequest,
                                         AuthenticatedUser authenticatedUser) {
        return taskManagerService.updateStatus(taskId, updateTaskStatusRequest.status());
    }
}
