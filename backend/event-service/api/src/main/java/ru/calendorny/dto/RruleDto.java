package ru.calendorny.dto;

import lombok.Builder;
import ru.calendorny.dto.enums.EventFrequency;
import java.time.DayOfWeek;


@Builder
public record RruleDto(
    EventFrequency frequency,
    DayOfWeek dayOfWeek,
    Integer dayOfMonth
) {
}
