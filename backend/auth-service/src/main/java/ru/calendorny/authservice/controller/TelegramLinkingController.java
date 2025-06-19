package ru.calendorny.authservice.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.authservice.service.TelegramLinkService.TelegramLinkService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TelegramLinkingController {

    private final TelegramLinkService telegramLinkService;

    @PostMapping("/tg-chat/{chatId}")
    public void registerChat(@PathVariable Long chatId, @RequestHeader("User-Token") String token) {
        telegramLinkService.saveTgChatId(token, chatId.toString());
    }

    @GetMapping("/tg-chat/{username}")
    public Long getChatId(@PathVariable UUID username) {
        return Long.parseLong(telegramLinkService.getChatId(username));
    }

}
