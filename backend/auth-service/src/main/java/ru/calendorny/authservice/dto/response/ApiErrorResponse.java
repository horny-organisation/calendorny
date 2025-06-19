package ru.calendorny.authservice.dto.response;

public record ApiErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage
) {
}
