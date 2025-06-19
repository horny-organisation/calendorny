package ru.calendorny.notificationservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.calendorny.notificationservice.exception.CustomApiException;
import ru.calendorny.notificationservice.response.ApiErrorResponse;
import ru.calendorny.notificationservice.response.ChatOperationResponse;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotAuthClient {

    private final BotAuthFeignClient feignClient;

    public ChatOperationResponse<Void> registerChat(Long chatId, String token) {
        try {
            feignClient.registerChat(chatId, token);
            return ChatOperationResponse.success(null);
        } catch (CustomApiException e) {
            ApiErrorResponse error = e.getApiErrorResponse();
            log.error(
                "Error during chat registration. Code: {} Response message: {}",
                error.code(),
                error.exceptionMessage());
            return ChatOperationResponse.error(error.exceptionMessage());
        } catch (Exception e) {
            log.error("Unexpected error during chat registration", e);
            return ChatOperationResponse.error("Unexpected error during chat registration");
        }
    }

    @Cacheable(value = "${cache.names.id}", key = "#username")
    public ChatOperationResponse<Long> getChatId(UUID username) {
        try {
            Long chatId = feignClient.getChatId(username);
            return ChatOperationResponse.success(chatId);
        } catch (CustomApiException e) {
            ApiErrorResponse error = e.getApiErrorResponse();
            log.error(
                "Error during getting chat id. Code: {} Response message: {}",
                error.code(),
                error.exceptionMessage());
            return ChatOperationResponse.error(error.exceptionMessage());
        } catch (Exception e) {
            log.error("Unexpected error during getting chat id", e);
            return ChatOperationResponse.error("Unexpected error during getting chat id");
        }
    }
}
