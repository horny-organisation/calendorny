package ru.calendorny.notificationservice.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.calendorny.notificationservice.client.BotAuthClient;
import ru.calendorny.notificationservice.entity.EventReminderRequest;
import ru.calendorny.notificationservice.entity.TodayTaskEvent;
import ru.calendorny.notificationservice.response.ChatOperationResponse;
import ru.calendorny.notificationservice.sender.MessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationsHandler {

    private final MessageSender sender;
    private final BotAuthClient botAuthClient;

    public void handleTaskUpdates(TodayTaskEvent request) {

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
            log.warn("Received empty chat id for task notification");
        } else {
            String message = getMessageTask(description, title, dueDate);
            sender.send(chatId, message);
        }
    }

    public void handleEventUpdates(EventReminderRequest request) {

        UUID userId = request.userId();
        String title = request.title();
        String location = request.location();
        LocalDateTime startTime = request.start();
        LocalDateTime endTime = request.end();

        ChatOperationResponse<Long> response = botAuthClient.getChatId(userId);
        Long chatId = null;

        if (response.success()) {
            chatId = response.data();
        }

        if (chatId == null) {
            log.warn("Received empty chat id for event notification");
        } else {
            String message = getMessageEvent(title, location, startTime, endTime);
            sender.send(chatId, message);
        }
    }

    private static @NotNull String getMessageTask(String description, String title, LocalDate dueDate) {
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

    private static @NotNull String getMessageEvent(String title, String location, LocalDateTime startTime, LocalDateTime endTime) {

        return """
            Notification on event!
            Title: %s
            Location: %s
            Start datetime: %s
            End datetime: %s"""
            .formatted(title, location, startTime, endTime);
    }
}
