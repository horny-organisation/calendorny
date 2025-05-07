package ru.calendorny.taskservice.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.TaskResponse;

@RequestMapping("/tasks")
public interface TaskApi {

    @PostMapping
    ResponseEntity<?> createNewTask(
            @RequestHeader("Authorization") String accessToken, @RequestBody CreateTaskRequest createTaskRequest);

    @GetMapping
    ResponseEntity<List<TaskResponse>> getTasksList(
            @RequestParam("from") LocalDateTime from,
            @RequestParam("to") LocalDateTime to,
            @RequestHeader("Authorization") String accessToken);

    @GetMapping("/{taskId}")
    ResponseEntity<TaskResponse> getTaskDetailsById(
            @PathVariable("taskId") UUID taskId, @RequestHeader("Authorization") String accessToken);

    @PatchMapping("/{taskId}")
    ResponseEntity<?> updateTaskById(
            @PathVariable("taskId") UUID taskId,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody UpdateTaskRequest updateTaskRequest);

    @DeleteMapping("/{taskId}")
    ResponseEntity<?> deleteTaskById(
            @PathVariable("taskId") UUID taskId, @RequestHeader("Authorization") String accessToken);

    @PatchMapping("/tasks/{taskId}/status")
    ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable("taskId") UUID taskId,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest);
}
