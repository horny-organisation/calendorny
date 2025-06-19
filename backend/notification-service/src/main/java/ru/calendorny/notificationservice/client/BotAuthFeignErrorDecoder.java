package ru.calendorny.notificationservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import ru.calendorny.notificationservice.exception.CustomApiException;
import ru.calendorny.notificationservice.response.ApiErrorResponse;

import java.io.IOException;

@Component
public class BotAuthFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(), ApiErrorResponse.class);
            return new CustomApiException(errorResponse);
        } catch (IOException e) {
            return new RuntimeException("Failed to parse error response from auth-service", e);
        }
    }
}
