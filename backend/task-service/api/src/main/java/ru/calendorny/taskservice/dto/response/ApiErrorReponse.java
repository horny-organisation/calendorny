package ru.calendorny.taskservice.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ApiErrorReponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stackTrace;
}
