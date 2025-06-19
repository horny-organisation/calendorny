package ru.calendorny.notificationservice;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.notificationservice.client.BotAuthClient;
import ru.calendorny.notificationservice.client.BotAuthFeignClient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CacheTests extends BaseTest {

    @Autowired
    BotAuthClient botAuthClient;

    @MockitoBean
    BotAuthFeignClient feignClient;

    @Test
    void testCaching() {
        UUID username = UUID.randomUUID();
        Long chatId = 123456789L;

        when(feignClient.getChatId(username)).thenReturn(chatId);

        botAuthClient.getChatId(username);
        botAuthClient.getChatId(username);

        verify(feignClient, times(1)).getChatId(username);

    }

}
