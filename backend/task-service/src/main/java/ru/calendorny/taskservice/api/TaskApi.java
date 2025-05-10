package ru.calendorny.taskservice.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
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
    ResponseEntity<TaskResponse> createNewTask(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody CreateTaskRequest createTaskRequest
    );

    @GetMapping
    ResponseEntity<List<TaskResponse>> getTasksList(
        @RequestParam("from") LocalDate fromDate,
        @RequestParam("to") LocalDate toDate,
        @RequestHeader("Authorization") String accessToken
    );

    @GetMapping("/{taskId}")
    ResponseEntity<TaskResponse> getTaskDetailsById(
        @PathVariable("taskId") UUID taskId,
        @RequestHeader("Authorization") String accessToken
    );

    @PatchMapping("/{taskId}")
    ResponseEntity<TaskResponse> updateTaskById(
        @PathVariable("taskId") UUID taskId,
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody UpdateTaskRequest updateTaskRequest
    );

    @DeleteMapping("/{taskId}")
    ResponseEntity<Void> deleteTaskById(
        @PathVariable("taskId") UUID taskId,
        @RequestHeader("Authorization") String accessToken
    );

    @PatchMapping("/{taskId}/status")
    ResponseEntity<TaskResponse> updateTaskStatus(
        @PathVariable("taskId") UUID taskId,
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest
    );
}
