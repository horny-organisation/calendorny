package ru.calendorny.notificationservice.enums;

import com.pengrad.telegrambot.model.BotCommand;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BotCommands {
    START("/start", "Start and register"),
    HELP("/help", "Show help");

    private final String command;
    private final String description;

    private static final Set<BotCommands> BOT_COMMANDS = EnumSet.allOf(BotCommands.class);

    public BotCommand toBotCommand() {
        return new BotCommand(command, description);
    }

    public static BotCommands fromCommand(String command) {
        for (BotCommands type : BOT_COMMANDS) {
            if (type.command.equals(command)) {
                return type;
            }
        }
        return null;
    }

    public static String generateHelpText() {
        StringBuilder helpText = new StringBuilder("The bot supports the following commands:\n");
        for (BotCommands botCommand : BOT_COMMANDS) {
            helpText.append(botCommand.command)
                    .append(" - ")
                    .append(botCommand.description)
                    .append("\n");
        }
        return helpText.toString();
    }
}
