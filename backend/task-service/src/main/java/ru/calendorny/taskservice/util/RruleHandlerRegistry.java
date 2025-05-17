package ru.calendorny.taskservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.util.rrulehandler.RruleHandler;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RruleHandlerRegistry {

    private final List<RruleHandler> handlers;

    public Optional<RruleHandler> findHandler(RruleDto.Frequency frequency) {
        return handlers.stream()
            .filter(handler -> handler.supports(frequency))
            .findFirst();
    }

    public void setKeyValue(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        for (RruleHandler handler : handlers) {
            handler.setToDto(key, value, rruleDtoBuilder);
        }
    }

    public void validate(RruleDto rruleDto) throws InvalidRruleException {
        findHandler(rruleDto.frequency())
            .ifPresent(handler -> handler.validate(rruleDto));
    }

    public void validateRruleString(String rruleString) throws InvalidRruleException {
        if (!rruleString.startsWith("FREQ=")) {
            throw new InvalidRruleException("RRULE must start with FREQ=");
        }

        String freqPart = rruleString.split("FREQ=")[1].split(";")[0];
        RruleDto.Frequency frequency;
        try {
            frequency = RruleDto.Frequency.valueOf(freqPart);
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid frequency in RRULE");
        }

        findHandler(frequency)
            .orElseThrow(() -> new InvalidRruleException("Unsupported frequency: " + frequency))
            .validateRruleString(rruleString);
    }

}
