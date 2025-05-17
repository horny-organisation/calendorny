package ru.calendorny.taskservice.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.exception.RruleParsingException;

@Component
@RequiredArgsConstructor
public class RruleCalculator {

    private final RruleHandlerRegistry rruleHandlerRegistry;

    public LocalDate findNextDate(String rruleString, LocalDate fromDate) {

        if (rruleString == null || rruleString.isBlank()) {
            throw new InvalidRruleException("Rrule can not be null/empty");
        }

        if (fromDate == null) {
            throw new IllegalArgumentException("Date can not be null");
        }
        try {

            rruleHandlerRegistry.validateRruleString(rruleString);

            RecurrenceRule rule = new RecurrenceRule(rruleString);

            ZonedDateTime zdt = fromDate.atStartOfDay(ZoneOffset.UTC);
            long millis = zdt.toInstant().toEpochMilli();
            DateTime start = new DateTime(millis);
            RecurrenceRuleIterator it = rule.iterator(start);

            while (it.hasNext()) {
                DateTime next = it.next();
                LocalDate nextDate = LocalDate.of(next.getYear(), next.getMonth() + 1, next.getDayOfMonth());

                if (nextDate.isAfter(fromDate)) {
                    return nextDate;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RruleParsingException("Rrule parsing exception: %s".formatted(e.getMessage()));
        }
    }
}
