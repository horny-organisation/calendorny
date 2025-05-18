package ru.calendorny.taskservice.dto;

import java.time.DayOfWeek;
import lombok.Builder;


@Builder
public record RruleDto (Frequency frequency,

    DayOfWeek dayOfWeek,

    Integer dayOfMonth){

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }
}
