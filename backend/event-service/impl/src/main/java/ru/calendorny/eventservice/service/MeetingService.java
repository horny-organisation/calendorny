package ru.calendorny.eventservice.service;

import ru.calendorny.dto.enums.MeetingType;
import ru.calendorny.eventservice.rabbit.dto.response.MeetingResponse;
import java.time.LocalDateTime;

public interface MeetingService {

    void sendMeetingRequest(MeetingType meetingType, Long eventId, LocalDateTime start);

    void processMeetingResponse(MeetingResponse meetingResponse);
}
