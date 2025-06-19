package ru.shcherbanev.nlpservice.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.shcherbanev.nlpservice.dto.NlpRequest;
import ru.shcherbanev.nlpservice.dto.NlpResponse;
import ru.shcherbanev.nlpservice.repository.NlpInteractionRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
public class NlpServiceTest {

    @Autowired
    private NlpService nlpService;

    @MockitoBean
    private NlpInteractionRepository nlpInteractionRepository;

    @Test
    void shouldExtractDateAndEventName() {
        NlpRequest request = new NlpRequest("tomorrow at 19:00 meet with mentor");

        NlpResponse response = nlpService.processPrompt(request);

        assertTrue(response.name().toLowerCase().contains("meet"));
        assertTrue(response.name().toLowerCase().contains("mentor"));
        assertNotNull(response.time());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        LocalDateTime expectedDateTime = now.plusDays(1)
            .withHour(19)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        assertTrue(Duration.between(expectedDateTime, response.time()).abs().toMinutes() <= 1);
    }
}
