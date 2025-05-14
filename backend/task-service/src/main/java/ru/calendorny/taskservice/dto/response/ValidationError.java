package ru.calendorny.taskservice.dto.response;

import lombok.Builder;

@Builder
public class ValidationError {

    String field;

    String message;
}
