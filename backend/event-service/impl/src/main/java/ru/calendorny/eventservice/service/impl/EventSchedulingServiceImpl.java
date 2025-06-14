package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;
import ru.calendorny.eventservice.service.EventSchedulingService;
import ru.calendorny.eventservice.util.rrule.RruleHandler;
import ru.calendorny.eventservice.util.rrule.RruleHandlerRegistry;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventSchedulingServiceImpl implements EventSchedulingService {

    private final JobSchedulerService jobSchedulerService;

    private final RruleHandlerRegistry handlerRegistry;

    public String scheduleEvent(LocalDateTime notificationTime, EventNotificationRequest request, RruleDto rruleDto) throws SchedulerException {
        if (rruleDto == null) {
            return jobSchedulerService.scheduleOneTime(request, notificationTime);
        }
        else {
            EventFrequency frequency = rruleDto.frequency();
            RruleHandler handler = handlerRegistry.findHandler(frequency)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported frequency: " + frequency));

            return handler.schedule(request, rruleDto);
        }
    }

}
