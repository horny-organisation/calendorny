package ru.calendorny.taskservice.api;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.calendorny.securitystarter.AuthenticatedUser;
import ru.calendorny.taskservice.dto.request.*;
import ru.calendorny.taskservice.dto.response.TaskResponse;

@RequestMapping("/api/v1/tasks")
public interface TaskApi {

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse createTask(
        @Valid @RequestBody CreateTaskRequest createTaskRequest,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    List<TaskResponse> getTasksByDateRange(
        @RequestParam("from") LocalDate fromDate,
        @RequestParam("to") LocalDate toDate,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse getTask(
        @PathVariable("taskId") UUID taskId,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);

    @PutMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateTask(
        @PathVariable("taskId") UUID taskId,
        @Valid @RequestBody UpdateTaskRequest updateTaskRequest,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);

    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTask(
        @PathVariable("taskId") UUID taskId,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateTaskStatus(
        @PathVariable("taskId") UUID taskId,
        @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest,
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser);
}
