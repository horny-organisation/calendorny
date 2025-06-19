package ru.shcherbanev.nlpservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NlpRequest(
    @NotBlank
    @Size(min = 5, max = 100, message = "prompt should be greater than {min} and less than {max}")
    String userPrompt
) {
}
