package ru.calendorny.eventservice.rrule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;
import ru.calendorny.eventservice.exception.ServiceException;

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
        if (!rruleString.startsWith(RruleConstants.FREQUENCY_PREFIX)) {
            throw new InvalidRruleException("RRULE must start with FREQ=");
        }

        String freqPart = rruleString.split(RruleConstants.FREQUENCY_PREFIX)[1].split(";")[0];
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

    public UUID schedule(EventInfo eventInfo, UUID userId, RruleDto rruleDto, LocalDateTime start, LocalDateTime end, Integer minutesBefore) throws SchedulerException {
        RruleHandler handler = findHandler(rruleDto.frequency()).orElseThrow(() -> new ServiceException("No such rrule handler for frequency: %s".formatted(rruleDto.frequency())));
        return  handler.schedule(eventInfo, userId, rruleDto, start, end, minutesBefore);
    }

}
