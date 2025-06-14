package ru.calendorny.eventservice.util.rrule;

import org.quartz.SchedulerException;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;

public interface RruleHandler {

    boolean supports(EventFrequency frequency);

    void append(RruleDto rruleDto, StringBuilder sb);

    void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder);

    void validate(RruleDto rruleDto);

    void validateRruleString(String rruleString);

    String schedule(EventNotificationRequest request, RruleDto rruleDto) throws SchedulerException;
}
