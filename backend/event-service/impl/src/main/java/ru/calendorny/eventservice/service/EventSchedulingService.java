package ru.calendorny.eventservice.service;

import org.quartz.SchedulerException;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventSchedulingService {

    //UUID scheduleEvent(LocalDateTime notificationTime, EventReminderRequest request, RruleDto rruleDto) throws SchedulerException;

    UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException;

    //void deleteJob(String jobId) throws SchedulerException;
}
