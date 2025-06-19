package ru.calendorny.notificationservice.handler;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.calendorny.notificationservice.enums.BotCommands;
import ru.calendorny.notificationservice.sender.MessageSender;
import ru.calendorny.notificationservice.service.BotCommandService;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CommandHandlerTest {

    private MessageSender sender;
    private BotCommandService startService;
    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        sender = mock(MessageSender.class);
        startService = mock(BotCommandService.class);

        when(startService.getCommand()).thenReturn(BotCommands.START);

        // Only one command service (START) for simplicity
        commandHandler = new CommandHandler(sender, List.of(startService));
    }

    @Test
    void handleValidStartCommand() {
        String commandText = "/start";

        // Mock Telegram update/message
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(commandText);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        // Call handler
        commandHandler.handle(List.of(update));

        // Verify command was routed correctly
        verify(startService, times(1)).handleCommand(123L, commandText);
        verify(sender, never()).send(anyLong(), anyString());
    }

    @Test
    void handleUnknownCommandSendsWarning() {
        String commandText = "/unknown";

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(commandText);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        commandHandler.handle(List.of(update));

        verify(sender).send(123L, "Unknown command");
        verifyNoMoreInteractions(startService);
    }

    @Test
    void handleExceptionThrownSendsErrorMessage() {
        String commandText = "/start";

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(commandText);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        // Simulate exception during command handling
        doThrow(new RuntimeException("Service failed"))
            .when(startService).handleCommand(123L, commandText);

        commandHandler.handle(List.of(update));

        verify(sender).send(123L, "Произошла ошибка. Пожалуйста, попробуйте позже.");
    }

    @Test
    void handleMessageWithoutCommandDoesNothing() {
        String nonCommandText = "Hello there";

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(nonCommandText);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);

        commandHandler.handle(List.of(update));

        verifyNoInteractions(sender);
        verifyNoInteractions(startService);
    }

    @Test
    void handleUpdateWithoutMessageDoesNothing() {
        Update update = mock(Update.class);
        when(update.message()).thenReturn(null);

        commandHandler.handle(List.of(update));

        verifyNoInteractions(sender);
        verifyNoInteractions(startService);
    }
}
