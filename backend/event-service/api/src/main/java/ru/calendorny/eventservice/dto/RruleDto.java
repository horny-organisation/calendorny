package ru.calendorny.eventservice.dto;

import lombok.Builder;
import ru.calendorny.eventservice.dto.enums.EventFrequency;

import java.time.DayOfWeek;


@Builder
public record RruleDto(
    EventFrequency frequency,
    DayOfWeek dayOfWeek,
    Integer dayOfMonth
) {
}
