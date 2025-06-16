package ru.calendorny.notificationservice.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageSender {
    private final TelegramBot bot;

    public void send(Long chatId, String message) {
        if (message != null) {
            SendMessage request = new SendMessage(chatId, message);
            bot.execute(request);
        }
    }
}
