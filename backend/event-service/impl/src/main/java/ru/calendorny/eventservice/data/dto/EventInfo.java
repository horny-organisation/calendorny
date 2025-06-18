package ru.calendorny.eventservice.data.dto;

import lombok.Builder;

@Builder
public record EventInfo(

    Long eventId,
    String title,
    String location
) {
}
