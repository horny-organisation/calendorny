package ru.calendorny.taskservice.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import jakarta.validation.constraints.Size;
import ru.calendorny.taskservice.dto.RruleDto;

public record CreateTaskRequest(

    @NotNull(message = "Task's title can not be null")
    @NotBlank(message = "Task's title can not be empty")
    @Size(max = 300, message = "Task title should not be more than {} characters")
    String title,

    @Size(max = 3000, message = "Task description should not be more than {} characters")
    String description,

    @NotNull(message = "Task's date can not be null")
    @FutureOrPresent(message = "Task's date can not be in past")
    LocalDate dueDate,

    RruleDto rrule
) {
}
