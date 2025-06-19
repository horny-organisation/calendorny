package ru.shcherbanev.nlpservice.dto;

import java.time.LocalDateTime;

public record NlpResponse(
    String name,
    LocalDateTime time
) {
}
