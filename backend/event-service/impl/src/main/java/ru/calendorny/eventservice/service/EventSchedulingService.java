package ru.calendorny.eventservice.service;

import org.quartz.SchedulerException;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import java.time.LocalDateTime;

public interface EventSchedulingService {

    String scheduleEvent(LocalDateTime notificationTime, EventNotificationRequest request, RruleDto rruleDto) throws SchedulerException;

    void deleteJob(String jobId) throws SchedulerException;
}
