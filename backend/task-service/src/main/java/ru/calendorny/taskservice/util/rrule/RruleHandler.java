package ru.calendorny.taskservice.util.rrule;

import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;

public interface RruleHandler {

    boolean supports(RruleDto.Frequency frequency);

    void append(RruleDto rruleDto, StringBuilder sb);

    void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder);

    void validate(RruleDto rruleDto) throws InvalidRruleException;

    void validateRruleString(String rruleString) throws InvalidRruleException;
}
