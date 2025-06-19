package ru.calendorny.eventservice.service;

import org.quartz.SchedulerException;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventSchedulingService {

    UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException;

    void deleteJob(UUID jobId) throws SchedulerException;
}
