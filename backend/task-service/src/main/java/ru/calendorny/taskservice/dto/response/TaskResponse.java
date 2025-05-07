package ru.calendorny.taskservice.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RecurrenceRule;

@Data
public class TaskResponse {

    UUID id;

    UUID userId;

    String title;

    String description;

    LocalDate dueDate;

    TaskStatus status;

    RecurrenceRule recurrenceRule;
}
