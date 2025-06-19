package ru.calendorny.googlemeetingservice.dto.request;

import java.time.LocalDateTime;

public record MeetingCreateRequest(Long eventId, LocalDateTime startDateTime) {}
