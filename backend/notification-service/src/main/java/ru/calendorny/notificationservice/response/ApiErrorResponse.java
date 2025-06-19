package ru.calendorny.notificationservice.response;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage
) {}
