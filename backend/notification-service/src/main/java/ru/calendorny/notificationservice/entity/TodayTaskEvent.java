package ru.calendorny.notificationservice.entity;

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
