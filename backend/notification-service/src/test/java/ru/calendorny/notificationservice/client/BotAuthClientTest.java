package ru.calendorny.notificationservice.client;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.calendorny.notificationservice.exception.CustomApiException;
import ru.calendorny.notificationservice.response.ApiErrorResponse;
import ru.calendorny.notificationservice.response.ChatOperationResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotAuthClientTest {

    private BotAuthFeignClient feignClient;
    private BotAuthClient botAuthClient;

    @BeforeEach
    void setUp() {
        feignClient = mock(BotAuthFeignClient.class);
        botAuthClient = new BotAuthClient(feignClient);
    }

    @Test
    void registerChatSuccess() {
        Long chatId = 123L;
        String token = "token";

        doNothing().when(feignClient).registerChat(chatId, token);

        ChatOperationResponse<Void> response = botAuthClient.registerChat(chatId, token);

        assertTrue(response.success());
        assertNull(response.data());
        verify(feignClient, times(1)).registerChat(chatId, token);
    }

    @Test
    void registerChatCustomApiException() {
        Long chatId = 123L;
        String token = "token";

        ApiErrorResponse error = new ApiErrorResponse(400, "", "Custom error occurred");
        CustomApiException exception = new CustomApiException(error);

        doThrow(exception).when(feignClient).registerChat(chatId, token);

        ChatOperationResponse<Void> response = botAuthClient.registerChat(chatId, token);

        assertFalse(response.success());
        assertEquals("Custom error occurred", response.message());
        verify(feignClient, times(1)).registerChat(chatId, token);
    }

    @Test
    void registerChatUnexpectedException() {
        Long chatId = 123L;
        String token = "token";

        doThrow(new RuntimeException("Unexpected")).when(feignClient).registerChat(chatId, token);

        ChatOperationResponse<Void> response = botAuthClient.registerChat(chatId, token);

        assertFalse(response.success());
        assertEquals("Unexpected error during chat registration", response.message());
        verify(feignClient, times(1)).registerChat(chatId, token);
    }

    @Test
    void getChatIdSuccess() {
        UUID username = UUID.randomUUID();
        Long expectedChatId = 999L;

        when(feignClient.getChatId(username)).thenReturn(expectedChatId);

        ChatOperationResponse<Long> response = botAuthClient.getChatId(username);

        assertTrue(response.success());
        assertEquals(expectedChatId, response.data());
        verify(feignClient, times(1)).getChatId(username);
    }

    @Test
    void getChatIdCustomApiException() {
        UUID username = UUID.randomUUID();

        ApiErrorResponse error = new ApiErrorResponse(404, "", "Chat not found");
        CustomApiException exception = new CustomApiException(error);

        when(feignClient.getChatId(username)).thenThrow(exception);

        ChatOperationResponse<Long> response = botAuthClient.getChatId(username);

        assertFalse(response.success());
        assertEquals("Chat not found", response.message());
        verify(feignClient, times(1)).getChatId(username);
    }

    @Test
    void getChatIdUnexpectedException() {
        UUID username = UUID.randomUUID();

        when(feignClient.getChatId(username)).thenThrow(new RuntimeException("DB down"));

        ChatOperationResponse<Long> response = botAuthClient.getChatId(username);

        assertFalse(response.success());
        assertEquals("Unexpected error during getting chat id", response.message());
        verify(feignClient, times(1)).getChatId(username);
    }
}
