package ru.calendorny.taskservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.calendorny.taskservice.enums.TaskStatus;

@Getter
public class UpdateTaskStatusRequest {

    @NotNull(message = "Task's status can not be null")
    private TaskStatus status;
}
