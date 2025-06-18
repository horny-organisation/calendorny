package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.exception.NotFoundException;
import ru.calendorny.eventservice.rabbit.dto.response.MeetingResponse;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.service.MeetingService;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final EventRepository eventRepository;

    @Override
    public void processMeetingResponse(MeetingResponse meetingResponse) {
        Long eventId = meetingResponse.eventId();
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        event.setVideoMeetingUrl(meetingResponse.link());
        eventRepository.save(event);
    }
}
