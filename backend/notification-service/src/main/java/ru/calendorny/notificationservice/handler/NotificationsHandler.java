package ru.calendorny.notificationservice.handler;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.calendorny.notificationservice.client.BotAuthClient;
import ru.calendorny.notificationservice.entity.TodayTaskEvent;
import ru.calendorny.notificationservice.response.ChatOperationResponse;
import ru.calendorny.notificationservice.sender.MessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationsHandler {

    private final MessageSender sender;
    private final BotAuthClient botAuthClient;

    public void handleUpdates(TodayTaskEvent request) {

        UUID userId = request.userId();
        String title = request.title();
        String description = request.description();
        LocalDate dueDate = request.dueDate();


        ChatOperationResponse<Long> response = botAuthClient.getChatId(userId);
        Long chatId = null;

        if (response.success()) {
            chatId = response.data();
        }

        if (chatId == null) {
            log.warn("Received empty chat ids");
        }

        String message = getMessage(description, title, dueDate);
        sender.send(chatId, message);
    }

    private static @NotNull String getMessage(String description, String title, LocalDate dueDate) {
        String message;
        if (description != null) {
            message =
                """
                    Notification on today task!
                    Title: %s
                    Description: %s
                    Due date: %s"""
                    .formatted(title, description, dueDate);
        } else {
            message =
                """
                    Notification on today task!
                    Title: %s
                    Due date: %s"""
                    .formatted(title, dueDate);
        }
        return message;
    }
}
