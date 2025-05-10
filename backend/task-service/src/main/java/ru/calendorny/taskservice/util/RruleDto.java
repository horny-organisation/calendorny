package ru.calendorny.taskservice.util;

import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;

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
