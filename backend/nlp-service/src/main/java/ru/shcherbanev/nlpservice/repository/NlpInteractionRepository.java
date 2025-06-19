package ru.shcherbanev.nlpservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.shcherbanev.nlpservice.document.NlpInteraction;

public interface NlpInteractionRepository extends MongoRepository<NlpInteraction, String> {
}
