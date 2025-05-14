package ru.calendorny.taskservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.taskservice.api.TaskApi;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.exception.UnauthorizedAccessException;
import ru.calendorny.taskservice.security.AuthenticatedUser;
import ru.calendorny.taskservice.service.TaskManagerService;

@RestController
public class TaskController implements TaskApi {

    private final TaskManagerService taskManagerService;

    public TaskController(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @Override
    public ResponseEntity<TaskResponse> createTask(String accessToken, CreateTaskRequest request) {
        UUID userId = getUserIdFromAccessToken();
        TaskResponse createdTask = taskManagerService.createTask(
                userId, request.getTitle(), request.getDescription(), request.getDueDate(), request.getRrule());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Override
    public ResponseEntity<List<TaskResponse>> getTasksByDateRange(LocalDate from, LocalDate to, String accessToken) {
        UUID userId = getUserIdFromAccessToken();
        List<TaskResponse> tasks = taskManagerService.getTasksByDateRange(userId, from, to);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @Override
    public ResponseEntity<TaskResponse> getTask(UUID taskId, String accessToken) {
        TaskResponse task = taskManagerService.getTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @Override
    public ResponseEntity<TaskResponse> updateTask(UUID taskId, String accessToken, UpdateTaskRequest request) {
        UUID userId = getUserIdFromAccessToken();
        TaskResponse updatedTask = taskManagerService.updateTask(
                taskId,
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getStatus(),
                request.getRrule());
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @Override
    public ResponseEntity<Void> deleteTask(UUID taskId, String accessToken) {
        taskManagerService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<TaskResponse> updateTaskStatus(
            UUID taskId, String accessToken, UpdateTaskStatusRequest request) {
        TaskResponse updatedTask = taskManagerService.updateStatus(taskId, request.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    private UUID getUserIdFromAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUser)) {
            throw new UnauthorizedAccessException();
        }
        return ((AuthenticatedUser) principal).id();
    }
}
