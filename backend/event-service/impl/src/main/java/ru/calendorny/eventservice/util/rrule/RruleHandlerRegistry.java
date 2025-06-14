package ru.calendorny.eventservice.util.rrule;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;

import static ru.calendorny.eventservice.util.rrule.RruleConstants.*;

@Component
@RequiredArgsConstructor
public class RruleHandlerRegistry {

    private final List<RruleHandler> handlers;

    public Optional<RruleHandler> findHandler(EventFrequency frequency) {
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
        if (!rruleString.startsWith(FREQUENCY_PREFIX)) {
            throw new InvalidRruleException("RRULE must start with FREQ=");
        }

        String freqPart = rruleString.split(FREQUENCY_PREFIX)[1].split(";")[0];
        EventFrequency frequency;
        try {
            frequency = EventFrequency.valueOf(freqPart);
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid frequency in RRULE");
        }

        findHandler(frequency)
            .orElseThrow(() -> new InvalidRruleException("Unsupported frequency: " + frequency))
            .validateRruleString(rruleString);
    }

}
