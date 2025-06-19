package ru.shcherbanev.nlpservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbanev.nlpservice.dto.NlpRequest;
import ru.shcherbanev.nlpservice.dto.NlpResponse;
import ru.shcherbanev.nlpservice.service.NlpService;

@RestController
@RequestMapping("api/v1/nlp")
@RequiredArgsConstructor
public class NlpController {

    private final NlpService nlpService;

    @PostMapping
    public NlpResponse process(@Valid NlpRequest nlpRequest) {
        return nlpService.processPrompt(nlpRequest);
    }

}
