package ru.calendorny.authservice.service.TelegramLinkService;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.authservice.config.BotProperties;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.exception.NotFoundException;
import ru.calendorny.authservice.repository.LinkInMemoryRepository;
import ru.calendorny.authservice.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class TelegramLinkService {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final BotProperties properties;
    private final LinkInMemoryRepository linkInMemoryRepository;
    private final ProfileRepository profileRepository;

    public String createLink(UUID userId) {
        String token = generate(properties.linkLength());
        linkInMemoryRepository.save(userId, token);
        return "%s?start=%s".formatted(properties.link(), token);
    }

    public void saveTgChatId(String token, String chatId) {
        UUID userUUID = linkInMemoryRepository.get(token);
        profileRepository.saveTgChatId(userUUID, chatId);
    }

    public String getChatId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("uuid %s not found".formatted(userId)));
        return profile.getTelegram();
    }

    private static String generate(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = ThreadLocalRandom.current().nextInt(LETTERS.length());
            sb.append(LETTERS.charAt(index));
        }
        return sb.toString();
    }

}
