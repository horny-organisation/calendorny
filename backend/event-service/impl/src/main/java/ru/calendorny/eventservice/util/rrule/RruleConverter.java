package ru.calendorny.eventservice.util.rrule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;
import static ru.calendorny.eventservice.util.rrule.RruleConstants.FREQUENCY_KEY;
import static ru.calendorny.eventservice.util.rrule.RruleConstants.FREQUENCY_PREFIX;

@Component
@RequiredArgsConstructor
@Converter
public class RruleConverter implements AttributeConverter<RruleDto, String> {

    private final RruleHandlerRegistry handlerRegistry;

    @Override
    public String convertToDatabaseColumn(RruleDto rruleDto) {
        if (rruleDto == null) return null;

        handlerRegistry.validate(rruleDto);

        StringBuilder sb = new StringBuilder();
        EventFrequency frequency = rruleDto.frequency();

        sb.append(FREQUENCY_PREFIX).append(frequency);

        handlerRegistry.findHandler(frequency)
            .ifPresent(handler -> handler.append(rruleDto, sb));

        return sb.toString();
    }

    @Override
    public RruleDto convertToEntityAttribute(String rruleString) {
        if (rruleString == null || rruleString.isBlank()) return null;

        handlerRegistry.validateRruleString(rruleString);

        String[] parts = rruleString.split(";");
        RruleDto.RruleDtoBuilder rruleDtoBuilder = RruleDto.builder();

        for (String part : parts) {
            if (part.isBlank()) continue;

            String[] kv = part.split("=");
            if (kv.length != 2) {
                throw new InvalidRruleException("Invalid RRULE part: " + part);
            }

            String key = kv[0].trim();
            String value = kv[1].trim();

            if (key.equals(FREQUENCY_KEY)) {
                rruleDtoBuilder.frequency(EventFrequency.valueOf(value));
            } else {
                handlerRegistry.setKeyValue(key, value, rruleDtoBuilder);
            }
        }

        RruleDto result = rruleDtoBuilder.build();
        handlerRegistry.validate(result);

        return result;
    }
}
