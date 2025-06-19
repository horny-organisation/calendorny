package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;
import ru.calendorny.eventservice.service.EventSchedulingService;
import ru.calendorny.eventservice.util.rrule.RruleHandlerRegistry;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSchedulingServiceImpl implements EventSchedulingService {

    private final JobSchedulerService jobSchedulerService;

    private final RruleHandlerRegistry handlerRegistry;

    @Override
    public UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException {
        if (rruleDto == null) {
            EventReminderRequest eventReminderRequest = EventReminderRequest.builder()
                .userId(userId)
                .eventId(eventInfo.eventId())
                .title(eventInfo.title())
                .location(eventInfo.location())
                .start(start)
                .end(end)
                .build();
            LocalDateTime reminderTime = start.minusMinutes(minutesBefore);
            return jobSchedulerService.scheduleOneTime(eventReminderRequest, reminderTime);
        } else {
            return handlerRegistry.schedule(eventInfo, userId, rruleDto, start, end, minutesBefore);
        }
    }

    @Override
    public void deleteJob(UUID jobId) throws SchedulerException {
        jobSchedulerService.deleteJob(jobId.toString());
    }
}
