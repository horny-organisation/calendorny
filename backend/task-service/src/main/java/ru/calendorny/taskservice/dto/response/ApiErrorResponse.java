package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiErrorResponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stackTrace;
}
