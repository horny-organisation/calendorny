package ru.calendorny.eventservice.rrule;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MonthlyRruleHandler implements RruleHandler {

    private final JobSchedulerService jobSchedulerService;

    @Override
    public boolean supports(EventFrequency frequency) {
        return frequency == EventFrequency.MONTHLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        Integer day = rruleDto.dayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new IllegalArgumentException("MONTHLY requires dayOfMonth");
        }
        sb.append(";").append(RruleConstants.BY_MONTHDAY_PREFIX).append(rruleDto.dayOfMonth());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(RruleConstants.BY_MONTHDAY_KEY)) {
            rruleDtoBuilder.dayOfMonth(Integer.valueOf(value));
        }
    }

    @Override
    public void validate(RruleDto rruleDto) throws InvalidRruleException {
        Integer day = rruleDto.dayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new InvalidRruleException("MONTHLY frequency requires valid dayOfMonth (1-31)");
        }
    }

    @Override
    public void validateRruleString(String rruleString) throws InvalidRruleException {
        if (!rruleString.contains(RruleConstants.BY_MONTHDAY_PREFIX)) {
            throw new InvalidRruleException("MONTHLY frequency requires BYMONTHDAY parameter");
        }
        try {
            String dayPart = rruleString.split(RruleConstants.BY_MONTHDAY_PREFIX)[1].split(";")[0];
            int day = Integer.parseInt(dayPart);
            if (day < 1 || day > 31) {
                throw new InvalidRruleException("BYMONTHDAY must be between 1 and 31");
            }
        } catch (NumberFormatException e) {
            throw new InvalidRruleException("Invalid BYMONTHDAY value - must be a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidRruleException("Invalid BYMONTHDAY format");
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid RRULE format: " + e.getMessage());
        }
    }

    @Override
    public UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException {
        EventReminderRequest eventReminderRequest = EventReminderRequest.builder()
            .userId(userId)
            .eventId(eventInfo.eventId())
            .title(eventInfo.title())
            .location(eventInfo.location())
            .build();
        LocalDateTime reminderTime = start.minusMinutes(minutesBefore);
        return jobSchedulerService.scheduleMonthly(eventReminderRequest, rruleDto.dayOfMonth(), reminderTime.toLocalTime());
    }
}
