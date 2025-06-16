package ru.calendorny.notificationservice.exception;

import lombok.Getter;
import ru.calendorny.notificationservice.response.ApiErrorResponse;

@Getter
public class CustomApiException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public CustomApiException(ApiErrorResponse apiErrorResponse) {
        super(apiErrorResponse.exceptionMessage());
        this.apiErrorResponse = apiErrorResponse;
    }

}
