package ru.calendorny.notificationservice.service;


import ru.calendorny.notificationservice.enums.BotCommands;

public interface BotCommandService {

    void handleCommand(Long chatId, String text);

    BotCommands getCommand();
}
