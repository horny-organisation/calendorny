package ru.calendorny.eventservice.util.rrule;

import java.time.DayOfWeek;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;
import ru.calendorny.eventservice.service.EventSchedulingService;

import static ru.calendorny.eventservice.util.rrule.RruleConstants.*;

@Component
@RequiredArgsConstructor
public class WeeklyRruleHandler implements RruleHandler {

    private final JobSchedulerService jobSchedulerService;

    @Override
    public boolean supports(EventFrequency frequency) {
        return frequency == EventFrequency.WEEKLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        if (rruleDto.dayOfWeek() == null) {
            throw new IllegalArgumentException("WEEKLY requires dayOfWeek");
        }
        sb.append(";").append(BY_DAY_PREFIX).append(rruleDto.dayOfWeek());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(BY_DAY_KEY)) {
            rruleDtoBuilder.dayOfWeek(DayOfWeek.valueOf(value));
        }
    }

    @Override
    public void validate(RruleDto rruleDto) throws InvalidRruleException {
        if (rruleDto.dayOfWeek() == null) {
            throw new InvalidRruleException("WEEKLY frequency requires dayOfWeek");
        }
    }

    @Override
    public void validateRruleString(String rruleString) throws InvalidRruleException {
        if (!rruleString.contains(BY_DAY_PREFIX)) {
            throw new InvalidRruleException("WEEKLY frequency requires BYDAY parameter");
        }

        try {
            String dayPart = rruleString.split(BY_DAY_PREFIX)[1].split(";")[0];
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayPart);
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid BYDAY value in WEEKLY rule");
        }
    }

    @Override
    public String schedule(EventNotificationRequest request, RruleDto rruleDto) throws SchedulerException {
        return jobSchedulerService.scheduleWeekly(request, rruleDto.dayOfWeek(), request.start().toLocalTime());
    }
}
