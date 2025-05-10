package ru.calendorny.taskservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.taskservice.api.TaskApi;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.service.JwtService;
import ru.calendorny.taskservice.service.TaskManagerService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskManagerService taskManagerService;

    private final JwtService jwtService;

    @Override
    public ResponseEntity<TaskResponse> createNewTask(String accessToken, CreateTaskRequest createTaskRequest) {
        UUID userId = jwtService.getUserIdFromAccessToken(accessToken);
        TaskResponse taskResponse = taskManagerService.createTask(userId, createTaskRequest.getTitle(), createTaskRequest.getDescription(), createTaskRequest.getDueDate(), createTaskRequest.getRrule());
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @Override
    public ResponseEntity<List<TaskResponse>> getTasksList(LocalDate from, LocalDate to, String accessToken) {
        UUID userId = jwtService.getUserIdFromAccessToken(accessToken);
        List<TaskResponse> taskResponseList = taskManagerService.getTasksListByUserIdInTimeInterval(userId, from, to);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseList);
    }

    @Override
    public ResponseEntity<TaskResponse> getTaskDetailsById(UUID taskId, String accessToken) {
        TaskResponse taskResponse= taskManagerService.getTask(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }

    @Override
    public ResponseEntity<TaskResponse> updateTaskById(UUID taskId, String accessToken, UpdateTaskRequest updateTaskRequest) {
        UUID userId = jwtService.getUserIdFromAccessToken(accessToken);
        TaskResponse taskResponse = taskManagerService.updateTask(taskId, updateTaskRequest.getTitle(), updateTaskRequest.getDescription(), updateTaskRequest.getDueDate(), updateTaskRequest.getStatus(), updateTaskRequest.getRecurrenceRule(), userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(taskResponse);
    }

    @Override
    public ResponseEntity<Void> deleteTaskById(UUID taskId, String accessToken) {
        taskManagerService.deleteTask(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<TaskResponse> updateTaskStatus(UUID taskId, String accessToken, UpdateTaskStatusRequest updateTaskStatusRequest) {
        TaskResponse taskResponse = taskManagerService.updateStatus(taskId, updateTaskStatusRequest.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }
}
