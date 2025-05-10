package ru.calendorny.taskservice.util;

import lombok.experimental.UtilityClass;
import java.time.DayOfWeek;

@UtilityClass
public class DayOfWeekConverter {

    public String convertDayOfWeek(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "MO";
            case TUESDAY -> "TU";
            case WEDNESDAY -> "WE";
            case THURSDAY -> "TH";
            case FRIDAY -> "FR";
            case SATURDAY -> "SA";
            case SUNDAY -> "SU";
        };
    }

    public DayOfWeek parseDayOfWeek(String str) {
        return switch (str) {
            case "MO" -> DayOfWeek.MONDAY;
            case "TU" -> DayOfWeek.TUESDAY;
            case "WE" -> DayOfWeek.WEDNESDAY;
            case "TH" -> DayOfWeek.THURSDAY;
            case "FR" -> DayOfWeek.FRIDAY;
            case "SA" -> DayOfWeek.SATURDAY;
            case "SU" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Unknown BYDAY: " + str);
        };
    }
}
