package ru.calendorny.taskservice.util;

import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import java.time.DayOfWeek;

@Component
public class WeeklyRruleHandler implements RruleHandler{

    public static final String BY_DAY_PREFIX = "BYDAY";

    @Override
    public boolean supports(RruleDto.Frequency frequency) {
        return frequency == RruleDto.Frequency.WEEKLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        if (rruleDto.dayOfWeek() == null) {
            throw new IllegalArgumentException("WEEKLY requires dayOfWeek");
        }
        sb.append(";").append(BY_DAY_PREFIX).append("=").append(rruleDto.dayOfWeek());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(BY_DAY_PREFIX)) {
            rruleDtoBuilder.dayOfWeek(DayOfWeek.valueOf(value));
        }
    }
}
