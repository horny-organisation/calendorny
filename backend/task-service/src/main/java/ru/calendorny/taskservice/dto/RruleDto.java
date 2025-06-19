package ru.calendorny.taskservice.dto;

import java.time.DayOfWeek;
import lombok.Builder;
import ru.calendorny.taskservice.enums.TaskFrequency;


@Builder
public record RruleDto(
    TaskFrequency frequency,
    DayOfWeek dayOfWeek,
    Integer dayOfMonth
) {
}
