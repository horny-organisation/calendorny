package ru.calendorny.taskservice.util;

import java.time.LocalDateTime;
import java.util.List;
import ru.calendorny.taskservice.enums.RecurrenceFrequency;
import ru.calendorny.taskservice.enums.WeekDay;

public class RecurrenceRule {

    RecurrenceFrequency freq;

    Integer interval;

    LocalDateTime until;

    Integer count;

    List<WeekDay> byDay;

    List<Integer> byMonthDay;

    List<Integer> byMnoth;

    WeekDay weekStart;
}
