package ru.calendorny.notificationservice.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.calendorny.notificationservice.enums.BotCommands;
import ru.calendorny.notificationservice.sender.MessageSender;
import ru.calendorny.notificationservice.service.BotCommandService;

@RequiredArgsConstructor
@Slf4j
@Component
public class CommandHandler {

    private final MessageSender sender;
    private final List<BotCommandService> services;

    public void handle(List<Update> updateList) {
        for (Update update : updateList) {
            if (update.message() != null) {
                Message message = update.message();
                long chatId = message.chat().id();


                try {
                    if (message.text().startsWith("/")) {
                        commandIdentifier(chatId, message.text());
                    }
                } catch (Exception e) {
                    log.error("Error while handling message: {}", message.text(), e);
                    sender.send(chatId, "Произошла ошибка. Пожалуйста, попробуйте позже.");
                }
            }
        }
    }

    private void commandIdentifier(Long chatId, String text) throws Exception {
        BotCommands command = BotCommands.fromCommand(text.split(" ")[0]);

        if (command == null) {
            log.warn("Unknown command: {}", text);
            sender.send(chatId, "Unknown command");
            return;
        }

        log.info("{} command", command.getCommand());

        BotCommandService service = findService(command);

        service.handleCommand(chatId, text);
    }

    private BotCommandService findService(BotCommands command) throws Exception {
        return services.stream()
            .filter(comm -> comm.getCommand().equals(command))
            .findFirst()
            .orElseThrow(() -> new Exception("Can't find handler for command: %s".formatted(command.getCommand())));
    }
}
