package ru.calendorny.eventservice.rrule;

import org.quartz.SchedulerException;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RruleHandler {

    boolean supports(EventFrequency frequency);

    void append(RruleDto rruleDto, StringBuilder sb);

    void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder);

    void validate(RruleDto rruleDto);

    void validateRruleString(String rruleString);

    UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException;
}
