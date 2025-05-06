package ru.calendorny.taskservice.dto.response;

import lombok.Data;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RecurrenceRule;
import java.time.LocalDate;
import java.util.UUID;

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
