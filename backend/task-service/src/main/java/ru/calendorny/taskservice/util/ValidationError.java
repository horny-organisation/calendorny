package ru.calendorny.taskservice.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidationError {

    String field;

    String message;
}
