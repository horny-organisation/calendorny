package ru.calendorny.taskservice.util.rrule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.util.rrulehandler.RruleHandlerRegistry;

import static ru.calendorny.taskservice.util.constant.RruleConstants.*;

@Component
@RequiredArgsConstructor
public class RruleConverter {

    private final RruleHandlerRegistry rruleHandlerRegistry;


    public String toRruleString(RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new InvalidRruleException("RRULE cannot be null");
        }

        rruleHandlerRegistry.validate(rruleDto);

        StringBuilder sb = new StringBuilder();
        TaskFrequency frequency = rruleDto.frequency();

        sb.append(FREQUENCY_PREFIX).append(frequency);

        rruleHandlerRegistry.findHandler(frequency)
                .ifPresent(handler -> handler.append(rruleDto, sb));

        return sb.toString();
    }

    public RruleDto toRruleDto(String rruleString) {
        if (rruleString == null || rruleString.isBlank()) {
            throw new InvalidRruleException("RRULE string cannot be null or empty");
        }
        rruleHandlerRegistry.validateRruleString(rruleString);

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
                rruleDtoBuilder.frequency(TaskFrequency.valueOf(value));
            } else {
                rruleHandlerRegistry.setKeyValue(key, value, rruleDtoBuilder);
            }
        }
        RruleDto result = rruleDtoBuilder.build();
        rruleHandlerRegistry.validate(result);

        return result;
    }
}
