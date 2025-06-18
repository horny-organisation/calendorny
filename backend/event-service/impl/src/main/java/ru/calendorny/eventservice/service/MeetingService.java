package ru.calendorny.eventservice.service;

import ru.calendorny.eventservice.dto.enums.MeetingType;
import ru.calendorny.eventservice.rabbit.dto.response.MeetingResponse;
import java.time.LocalDateTime;

public interface MeetingService {

    void processMeetingResponse(MeetingResponse meetingResponse);
}
