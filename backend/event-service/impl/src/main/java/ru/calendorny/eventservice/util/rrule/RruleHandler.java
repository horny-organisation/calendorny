package ru.calendorny.eventservice.util.rrule;

import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.EventFrequency;

public interface RruleHandler {

    boolean supports(EventFrequency frequency);

    void append(RruleDto rruleDto, StringBuilder sb);

    void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder);

    void validate(RruleDto rruleDto);

    void validateRruleString(String rruleString);
}
