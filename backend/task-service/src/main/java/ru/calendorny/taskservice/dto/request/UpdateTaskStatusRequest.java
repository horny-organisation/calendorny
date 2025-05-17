package ru.calendorny.taskservice.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.calendorny.taskservice.enums.TaskStatus;

public record UpdateTaskStatusRequest(

    @NotNull(message = "Task's status can not be null")
    TaskStatus status
) {
}
