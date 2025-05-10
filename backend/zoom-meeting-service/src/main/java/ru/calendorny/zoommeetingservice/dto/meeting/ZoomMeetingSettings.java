package ru.calendorny.zoommeetingservice.dto.meeting;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZoomMeetingSettings(@JsonProperty("join_before_host") boolean joinBeforeHost) {}
