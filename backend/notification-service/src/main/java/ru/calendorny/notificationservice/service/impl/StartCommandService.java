package ru.calendorny.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.calendorny.notificationservice.client.BotAuthClient;
import ru.calendorny.notificationservice.enums.BotCommands;
import ru.calendorny.notificationservice.response.ChatOperationResponse;
import ru.calendorny.notificationservice.sender.MessageSender;
import ru.calendorny.notificationservice.service.BotCommandService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartCommandService implements BotCommandService {

    private final MessageSender sender;
    private final BotAuthClient botAuthClient;

    @Override
    public void handleCommand(Long chatId, String text) {
        sender.send(chatId, "Registration started");

        String[] parts = text.split(" ");
        if (parts.length > 1) {
            String token = parts[1];

            ChatOperationResponse<Void> res = botAuthClient.registerChat(chatId, token);

            if (res.success()) {
                sender.send(chatId, "Telegram успешно привязан к вашему аккаунту.");
                log.info("User registration completed");
            } else {
                sender.send(chatId, "Ошибка при привязке: " + res.message());
                log.error("User registration failed: reason: {}", res.message());
            }
        } else {
            sender.send(chatId, "Токен не найден. Пожалуйста, перейдите по корректной ссылке.");
            log.warn("Start command received without token");
        }
    }

    @Override
    public BotCommands getCommand() {
        return BotCommands.START;
    }
}
