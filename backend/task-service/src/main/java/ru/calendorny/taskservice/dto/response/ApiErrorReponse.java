package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class ApiErrorReponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stackTrace;
}
