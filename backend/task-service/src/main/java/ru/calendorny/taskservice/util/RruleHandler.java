package ru.calendorny.taskservice.util;

import ru.calendorny.taskservice.dto.RruleDto;

public interface RruleHandler {

    boolean supports(RruleDto.Frequency frequency);

    void append(RruleDto rruleDto, StringBuilder sb);

    void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder);
}
