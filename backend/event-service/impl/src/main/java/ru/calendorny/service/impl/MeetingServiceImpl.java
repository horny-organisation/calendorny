package ru.calendorny.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.dto.enums.MeetingType;
import ru.calendorny.exception.ServiceException;
import ru.calendorny.rabbit.dto.request.MeetingCreateRequest;
import ru.calendorny.rabbit.dto.response.MeetingResponse;
import ru.calendorny.rabbit.producer.RabbitMeetingProducer;
import ru.calendorny.service.EventManagementService;
import ru.calendorny.service.MeetingService;
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
