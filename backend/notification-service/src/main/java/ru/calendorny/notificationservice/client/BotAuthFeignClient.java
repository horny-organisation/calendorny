package ru.calendorny.notificationservice.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.calendorny.notificationservice.config.FeignConfig;

@FeignClient(name = "auth-service", path = "/api/v1", configuration = FeignConfig.class)
public interface BotAuthFeignClient {

    @PostMapping("/tg-chat/{chatId}")
    void registerChat(@PathVariable Long chatId, @RequestHeader("User-Token") String token);

    @GetMapping("/tg-chat/{username}")
    Long getChatId(@PathVariable UUID username);
}
