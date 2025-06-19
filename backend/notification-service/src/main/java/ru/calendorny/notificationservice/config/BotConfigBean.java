package ru.calendorny.notificationservice.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfigBean {
    @Bean
    public TelegramBot telegramBot(BotConfig botConfig) {
        return new TelegramBot(botConfig.telegramToken());
    }
}
