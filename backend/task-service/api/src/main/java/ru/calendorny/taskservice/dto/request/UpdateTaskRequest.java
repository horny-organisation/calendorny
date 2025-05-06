package ru.calendorny.taskservice.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RecurrenceRule;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
public class UpdateTaskRequest {

    private String title;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    private RecurrenceRule recurrenceRule;
}
