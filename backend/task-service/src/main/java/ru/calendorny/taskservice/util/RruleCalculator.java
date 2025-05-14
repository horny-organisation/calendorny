package ru.calendorny.taskservice.util;

import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import ru.calendorny.taskservice.exception.InvalidDateException;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.exception.RruleParsingException;

@UtilityClass
public class RruleCalculator {

    public LocalDate findNextDate(String rruleString, LocalDate fromDate) {

        if (rruleString == null || rruleString.isBlank()) {
            throw new InvalidRruleException("Rrule can not be null/empty");
        }

        if (fromDate == null) {
            throw new InvalidDateException("Date can not be null");
        }
        try {

            RecurrenceRule rule = new RecurrenceRule(rruleString);
            DateTime start = new DateTime(fromDate.getYear(), fromDate.getMonthValue() - 1, fromDate.getDayOfMonth());

            RecurrenceRuleIterator it = rule.iterator(start);

            while (it.hasNext()) {
                DateTime next = it.next();
                LocalDate nextDate = LocalDate.of(next.getYear(), next.getMonth() + 1, next.getDayOfMonth());

                if (!nextDate.isBefore(fromDate)) {
                    return nextDate;
                }
            }

            throw new RruleParsingException("No next date");
        } catch (Exception e) {
            throw new RruleParsingException("Rrule parsing exception: %s".formatted(e.getMessage()));
        }
    }
}
