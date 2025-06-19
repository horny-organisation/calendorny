package ru.shcherbanev.nlpservice.document;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nlp_interactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NlpInteraction {
    @Id
    private String id;
    private String userPrompt;
    private String eventName;
    private LocalDateTime eventDate;
    private List<String> namesByUserFromCaptcha;
    private List<LocalDateTime> eventDateByUserFromCaptcha;
}
