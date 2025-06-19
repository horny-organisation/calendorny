package ru.calendorny.eventservice.service;

import ru.calendorny.eventservice.rabbit.dto.response.MeetingResponse;


public interface MeetingService {

    void processMeetingResponse(MeetingResponse meetingResponse);
}
