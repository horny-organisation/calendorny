package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.dto.enums.MeetingType;
import ru.calendorny.eventservice.exception.ServiceException;
import ru.calendorny.eventservice.rabbit.dto.request.MeetingCreateRequest;
import ru.calendorny.eventservice.rabbit.dto.response.MeetingResponse;
import ru.calendorny.eventservice.rabbit.producer.RabbitMeetingProducer;
import ru.calendorny.eventservice.service.EventManagementService;
import ru.calendorny.eventservice.service.MeetingService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final RabbitMeetingProducer meetingProducer;

    private EventManagementService eventManagementService;
    @Override
    public void sendMeetingRequest(MeetingType meetingType, Long eventId, LocalDateTime start) {
        MeetingCreateRequest request = MeetingCreateRequest.builder()
            .eventId(eventId)
            .startDateTime(start)
            .build();
        switch (meetingType) {
            case GOOGLE -> meetingProducer.sendGoogleMeetingCreationRequest(request);
            case ZOOM -> meetingProducer.sendZoomMeetingCreationRequest(request);
            default -> throw new ServiceException("No such meeting type: %s".formatted(meetingType));
        }
    }

    @Override
    public void processMeetingResponse(MeetingResponse meetingResponse) {
        eventManagementService.setVideoMeetingLinkToEvent(meetingResponse.eventId(), meetingResponse.link());
    }
}
