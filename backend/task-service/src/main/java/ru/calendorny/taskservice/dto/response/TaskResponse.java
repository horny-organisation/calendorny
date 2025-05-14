package ru.calendorny.taskservice.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskStatus;

@Getter
@Setter
public class TaskResponse {

    UUID id;

    UUID userId;

    String title;

    String description;

    LocalDate dueDate;

    TaskStatus status;

    RruleDto recurrenceRule;
}
