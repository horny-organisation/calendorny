package ru.calendorny.zoommeetingservice.dto.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZoomMeetingRequest(
        String topic,
        int type,
        @JsonProperty("start_time") String startTime,
        String timezone,
        ZoomMeetingSettings settings) {}
