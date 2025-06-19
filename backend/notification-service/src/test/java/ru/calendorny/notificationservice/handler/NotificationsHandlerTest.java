package ru.calendorny.notificationservice.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.calendorny.notificationservice.client.BotAuthClient;
import ru.calendorny.notificationservice.entity.EventReminderRequest;
import ru.calendorny.notificationservice.entity.TodayTaskEvent;
import ru.calendorny.notificationservice.response.ChatOperationResponse;
import ru.calendorny.notificationservice.sender.MessageSender;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NotificationsHandlerTest {

    private MessageSender sender;
    private BotAuthClient botAuthClient;
    private NotificationsHandler handler;

    @BeforeEach
    void setUp() {
        sender = mock(MessageSender.class);
        botAuthClient = mock(BotAuthClient.class);
        handler = new NotificationsHandler(sender, botAuthClient);
    }

    @Test
    void handleTaskUpdatesWithChatIdSendsMessage() {
        UUID userId = UUID.randomUUID();
        LocalDate dueDate = LocalDate.now();

        TodayTaskEvent taskEvent = new TodayTaskEvent(UUID.randomUUID(), userId, "Task Title", "Task Description", dueDate);
        when(botAuthClient.getChatId(userId)).thenReturn(ChatOperationResponse.success(123L));

        handler.handleTaskUpdates(taskEvent);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(sender).send(eq(123L), messageCaptor.capture());

        String message = messageCaptor.getValue();
        assertTrue(message.contains("Task Title"));
        assertTrue(message.contains("Task Description"));
        assertTrue(message.contains(dueDate.toString()));
    }

    @Test
    void handleTaskUpdatesWithoutChatIdDoesNotSend() {
        UUID userId = UUID.randomUUID();
        TodayTaskEvent taskEvent = new TodayTaskEvent(UUID.randomUUID(), userId, "Task Title", "Desc", LocalDate.now());

        when(botAuthClient.getChatId(userId)).thenReturn(ChatOperationResponse.success(null));

        handler.handleTaskUpdates(taskEvent);

        verify(sender, never()).send(anyLong(), anyString());
    }

    @Test
    void handleEventUpdatesWithChatIdSendsMessage() {
        UUID userId = UUID.randomUUID();
        EventReminderRequest eventRequest = new EventReminderRequest(0L, userId, "Meeting", "Conference Room");

        when(botAuthClient.getChatId(userId)).thenReturn(ChatOperationResponse.success(777L));

        handler.handleEventUpdates(eventRequest);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(sender).send(eq(777L), messageCaptor.capture());

        String message = messageCaptor.getValue();
        assertTrue(message.contains("Meeting"));
        assertTrue(message.contains("Conference Room"));
    }

    @Test
    void handleEventUpdatesWithoutChatIdDoesNotSend() {
        UUID userId = UUID.randomUUID();
        EventReminderRequest eventRequest = new EventReminderRequest(0L, userId, "Meeting", "Location");

        when(botAuthClient.getChatId(userId)).thenReturn(ChatOperationResponse.success(null));

        handler.handleEventUpdates(eventRequest);

        verify(sender, never()).send(anyLong(), anyString());
    }
}
