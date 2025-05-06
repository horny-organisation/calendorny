package ru.calendorny.taskservice.dto.response;

import lombok.Data;
import ru.calendorny.taskservice.util.ValidationError;
import java.util.List;

@Data
public class ValidationErrorResponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stacktrace;

    List<ValidationError> validationErrors;
}
