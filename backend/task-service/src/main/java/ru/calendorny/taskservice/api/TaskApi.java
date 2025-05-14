package ru.calendorny.taskservice.api;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.calendorny.taskservice.dto.request.*;
import ru.calendorny.taskservice.dto.response.TaskResponse;

@RequestMapping("/api/v1/tasks")
public interface TaskApi {

    @PostMapping
    ResponseEntity<TaskResponse> createTask(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @Valid @RequestBody CreateTaskRequest createTaskRequest);

    @GetMapping
    ResponseEntity<List<TaskResponse>> getTasksByDateRange(
            @RequestParam("from") LocalDate fromDate,
            @RequestParam("to") LocalDate toDate,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

    @GetMapping("/{taskId}")
    ResponseEntity<TaskResponse> getTask(
            @PathVariable("taskId") UUID taskId, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

    @PutMapping("/{taskId}")
    ResponseEntity<TaskResponse> updateTask(
            @PathVariable("taskId") UUID taskId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest);

    @DeleteMapping("/{taskId}")
    ResponseEntity<Void> deleteTask(
            @PathVariable("taskId") UUID taskId, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

    @PatchMapping("/{taskId}/status")
    ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable("taskId") UUID taskId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
            @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest);
}
