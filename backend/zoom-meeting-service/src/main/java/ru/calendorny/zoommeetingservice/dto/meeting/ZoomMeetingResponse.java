package ru.calendorny.zoommeetingservice.dto.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZoomMeetingResponse(@JsonProperty("join_url") String joinUrl) {}
