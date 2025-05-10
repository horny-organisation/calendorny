package ru.calendorny.taskservice.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.calendorny.taskservice.util.RruleDto;

@Getter
public class CreateTaskRequest {

    @NotNull(message = "Task's title can not be null")
    @NotBlank(message = "Task's title can not be empty")
    private String title;

    @NotNull(message = "Task's description can not be null")
    @NotBlank(message = "Task's description can not be empty")
    private String description;

    @NotNull(message = "Task's date can not be null")
    @FutureOrPresent(message = "Task's date can not be in past")
    private LocalDate dueDate;

    private RruleDto rrule;
}
