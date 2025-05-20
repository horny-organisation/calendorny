package ru.calendorny.taskservice.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskStatus;

@Builder
public record TaskResponse(
    UUID id,
    UUID userId,
    String title,
    String description,
    LocalDate dueDate,
    TaskStatus status,
    RruleDto recurrenceRule
) {
}
