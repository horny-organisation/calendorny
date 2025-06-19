package ru.calendorny.eventservice.rabbit.dto.request;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record MeetingCreateRequest(Long eventId, LocalDateTime startDateTime) {}
