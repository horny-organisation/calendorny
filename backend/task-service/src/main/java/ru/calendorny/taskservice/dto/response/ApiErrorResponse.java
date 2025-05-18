package ru.calendorny.taskservice.dto.response;

import lombok.Builder;

@Builder
public record ApiErrorResponse (

    String description,

    String code,

    String exceptionName,

    String exceptionMessage
){}
