package ru.calendorny.taskservice.dto;

import java.time.DayOfWeek;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RruleDto {

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    private Frequency frequency;

    private DayOfWeek dayOfWeek;

    private Integer dayOfMonth;
}
