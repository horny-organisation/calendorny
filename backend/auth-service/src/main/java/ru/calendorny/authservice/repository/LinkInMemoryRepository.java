package ru.calendorny.authservice.repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class LinkInMemoryRepository {

    private final Map<String, UUID> map = new ConcurrentHashMap<>();

    public void save(UUID userId, String token) {
        map.put(token, userId);
    }

    public UUID get(String token) {
        return map.get(token);
    }

}
