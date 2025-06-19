package ru.calendorny.eventservice.rrule;

import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.exception.ServiceException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RruleEventCalculator {

    private final RruleConverter rruleConverter;

    public List<EventShortResponse> generateOccurrences(EventDetailedResponse event, LocalDateTime fromDate, LocalDateTime toDate) {
        List<EventShortResponse> occurrences = new ArrayList<>();

        if (event.rrule() == null) {
            if (!event.startTime().isAfter(toDate) && !event.endTime().isBefore(fromDate)) {
                occurrences.add(toShortResponse(event, event.startTime(), event.endTime()));
            }
            return occurrences;
        }

        String rruleString = rruleConverter.convertToDatabaseColumn(event.rrule());
        LocalDateTime firstStart = event.startTime();
        long durationNanos = java.time.Duration.between(event.startTime(), event.endTime()).toNanos();

        DateTime rfcStart = new DateTime(firstStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        RecurrenceRuleIterator it = null;
        try {
            it = new RecurrenceRule(rruleString).iterator(rfcStart);
        } catch (InvalidRecurrenceRuleException e) {
            throw new ServiceException("Invalid rrule: %s".formatted(rruleString));
        }

        long fromMillis = fromDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long toMillis = toDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        while (it.hasNext()) {
            DateTime next = it.nextDateTime();
            long startMillis = next.getTimestamp();
            if (startMillis >= toMillis) break;

            if (startMillis >= fromMillis) {
                LocalDateTime start = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(startMillis),
                    ZoneId.systemDefault()
                );
                LocalDateTime end = start.plusNanos(durationNanos);
                occurrences.add(toShortResponse(event, start, end));
            }
        }


        return occurrences;
    }

    private EventShortResponse toShortResponse(EventDetailedResponse original, LocalDateTime newStart, LocalDateTime newEnd) {
        return EventShortResponse.builder()
            .id(original.id())
            .title(original.title())
            .location(original.location())
            .startTime(newStart)
            .endTime(newEnd)
            .labels(original.labels())
            .build();
    }
}
