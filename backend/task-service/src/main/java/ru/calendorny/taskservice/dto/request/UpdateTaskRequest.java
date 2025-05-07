package ru.calendorny.taskservice.dto.request;

import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RecurrenceRule;

@Getter
@EqualsAndHashCode
public class UpdateTaskRequest {

    private String title;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    private RecurrenceRule recurrenceRule;
}
