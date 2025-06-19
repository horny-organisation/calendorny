package ru.calendorny.eventservice.service;

import ru.calendorny.eventservice.dto.LabelDto;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventInfoRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventReminderRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import java.awt.Label;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventManagementService {

    EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest);

    List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDateTime from, LocalDateTime to);

    EventDetailedResponse getEventDetailedInfoById(UUID userId, Long eventId);

    void deleteEventById(UUID userId, Long eventId);

    void updateEventInfo(UUID userId, Long eventId, UpdateEventInfoRequest updateEventInfoRequest);

    void updateEventReminder(UUID userId, Long eventId, UpdateEventReminderRequest updateEventReminderRequest);

    List<LabelDto> getAllLabels();
}
