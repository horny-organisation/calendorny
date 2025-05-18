package ru.calendorny.taskservice.dto.response;

import lombok.Builder;

@Builder
public record ValidationError(
    String field,
    String message
) {}
