package ru.calendorny.taskservice.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.util.RruleDto;

@Getter
public class UpdateTaskRequest {

    private String title;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    private RruleDto recurrenceRule;
}
