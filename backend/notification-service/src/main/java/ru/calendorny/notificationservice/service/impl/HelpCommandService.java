package ru.calendorny.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.notificationservice.enums.BotCommands;
import ru.calendorny.notificationservice.sender.MessageSender;
import ru.calendorny.notificationservice.service.BotCommandService;
import static ru.calendorny.notificationservice.messages.BotMessages.HELP_TEXT;

@Service
@RequiredArgsConstructor
public class HelpCommandService implements BotCommandService {

    private final MessageSender sender;

    @Override
    public void handleCommand(Long chatId, String text) {
        sender.send(chatId, HELP_TEXT);
    }

    @Override
    public BotCommands getCommand() {
        return BotCommands.HELP;
    }
}
