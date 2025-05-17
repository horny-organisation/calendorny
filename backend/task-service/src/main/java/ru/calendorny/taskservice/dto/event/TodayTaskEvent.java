package ru.calendorny.taskservice.dto.event;

import java.time.LocalDate;
import java.util.UUID;

public record TodayTaskEvent(
    UUID taskId,
    UUID userId,
    String title,
    String description,
    LocalDate dueDate
) {
}
