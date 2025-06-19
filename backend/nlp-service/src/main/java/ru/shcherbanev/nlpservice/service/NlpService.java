package ru.shcherbanev.nlpservice.service;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.shcherbanev.nlpservice.document.NlpInteraction;
import ru.shcherbanev.nlpservice.dto.NlpRequest;
import ru.shcherbanev.nlpservice.dto.NlpResponse;
import ru.shcherbanev.nlpservice.repository.NlpInteractionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NlpService {

    private final Parser parser;
    private final StanfordCoreNLP pipeline;
    private final NlpInteractionRepository repository;
    private final Executor executorPool;

    public NlpResponse processPrompt(NlpRequest request) {
        log.debug("Processing NLP request for prompt: {}", request.userPrompt());
        CoreDocument doc = new CoreDocument(request.userPrompt());
        pipeline.annotate(doc);

        StringBuilder eventName = new StringBuilder();
        String dateString = null;

        for (CoreEntityMention em : doc.entityMentions()) {
            if (em.entityType().equals("DATE") || em.entityType().equals("TIME")) {
                dateString = em.text();
            } else {
                eventName.append(em.text()).append(" ");
            }
        }

        if (eventName.isEmpty()) {
            eventName.append(request.userPrompt());
        }

        LocalDateTime dateTime = LocalDateTime.now();

        if (dateString != null && !dateString.isBlank()) {
            List<DateGroup> groups = parser.parse(dateString);
            if (!groups.isEmpty() && !groups.get(0).getDates().isEmpty()) {
                Date extracted = groups.get(0).getDates().get(0);
                dateTime = LocalDateTime.ofInstant(extracted.toInstant(), ZoneId.of("Europe/Moscow"));
            }
        }
        NlpResponse response = new NlpResponse(eventName.toString().trim(), dateTime);
        log.info("Processed NLP request. Event: {}, Date: {}", response.name(), response.time());

        CompletableFuture.runAsync(() ->
                saveToDb(request, response), executorPool)
            .exceptionally(ex -> {
                log.warn("Async save operation failed", ex);
                return null;
            });

        return response;
    }

    private void saveToDb(NlpRequest request, NlpResponse response) {
        try {
            repository.save(NlpInteraction.builder()
                .userPrompt(request.userPrompt())
                .eventName(response.name())
                .eventDate(response.time())
                .build());
            log.debug("Successfully saved interaction to DB");
        } catch (Exception e) {
            log.warn("Save operation failed", e);
            throw e;
        }
    }

}
