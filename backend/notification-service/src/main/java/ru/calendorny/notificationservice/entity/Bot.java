package ru.calendorny.notificationservice.entity;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.calendorny.notificationservice.config.BotConfig;
import ru.calendorny.notificationservice.config.BotConfigBean;
import ru.calendorny.notificationservice.enums.BotCommands;
import ru.calendorny.notificationservice.handler.CommandHandler;
import java.util.Arrays;

@Slf4j
@Component
public class Bot {

    private final TelegramBot bot;
    private final CommandHandler handler;

    public Bot(BotConfig botConfig, BotConfigBean configBean, CommandHandler handler) {
        bot = configBean.telegramBot(botConfig);
        this.handler = handler;
    }

    @PostConstruct
    public void init() {
        BotCommand[] commands = Arrays.stream(BotCommands.values())
                .map(BotCommands::toBotCommand)
                .toArray(BotCommand[]::new);

        SetMyCommands request = new SetMyCommands(commands);
        bot.execute(request);

        bot.setUpdatesListener(updates -> {
            try {
                handler.handle(updates);
            } catch (Exception e) {
                log.error("Exception while handling updates", e);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
