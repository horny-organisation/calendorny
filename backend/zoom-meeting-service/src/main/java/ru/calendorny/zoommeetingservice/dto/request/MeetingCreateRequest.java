package ru.calendorny.zoommeetingservice.dto.request;

import java.time.LocalDateTime;

public record MeetingCreateRequest(Long eventId, LocalDateTime startDateTime) {}
