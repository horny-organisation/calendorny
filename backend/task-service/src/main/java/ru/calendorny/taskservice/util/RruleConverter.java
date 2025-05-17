package ru.calendorny.taskservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;

@Component
@RequiredArgsConstructor
public class RruleConverter {

    public static final String FREQUENCY_PREFIX = "FREQ";

    private final RruleHandlerRegistry rruleHandlerRegistry;


    public String toRruleString(RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new InvalidRruleException("RRULE cannot be null");
        }

        StringBuilder sb = new StringBuilder();
        RruleDto.Frequency frequency = rruleDto.frequency();

        sb.append(FREQUENCY_PREFIX).append("=").append(frequency);

        rruleHandlerRegistry.findHandler(frequency)
                .ifPresent(handler -> handler.append(rruleDto, sb));

        return sb.toString();
    }

    public RruleDto toRruleDto(String rruleString) {
        if (rruleString == null || rruleString.isBlank()) {
            throw new InvalidRruleException("RRULE string cannot be null or empty");
        }

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

            if (key.equals(FREQUENCY_PREFIX)) {
                rruleDtoBuilder.frequency(RruleDto.Frequency.valueOf(value));
            } else {
                rruleHandlerRegistry.setKeyValue(key, value, rruleDtoBuilder);
            }
        }

        return rruleDtoBuilder.build();
    }
}
