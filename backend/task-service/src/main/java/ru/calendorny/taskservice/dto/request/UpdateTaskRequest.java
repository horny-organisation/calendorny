package ru.calendorny.taskservice.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskStatus;

@Getter
public class UpdateTaskRequest {

    @NotNull(message = "Task's title can not be null")
    @NotBlank(message = "Task's title can not be empty")
    private String title;

    @NotNull(message = "Task's description can not be null")
    @NotBlank(message = "Task's description can not be empty")
    private String description;

    @NotNull(message = "Task's date can not be null")
    @FutureOrPresent(message = "Task's date can not be in past")
    private LocalDate dueDate;

    @NotNull(message = "Task's status can not be null")
    private TaskStatus status;

    private RruleDto rrule;
}
