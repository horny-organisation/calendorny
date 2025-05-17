package ru.calendorny.taskservice.util;

import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;

@Component
public class MonthlyRruleHandler implements RruleHandler{

    public static final String BY_MONTHDAY_PREFIX = "BYMONTHDAY";

    @Override
    public boolean supports(RruleDto.Frequency frequency) {
        return frequency == RruleDto.Frequency.WEEKLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        Integer day = rruleDto.dayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new IllegalArgumentException("MONTHLY requires dayOfMonth");
        }
        sb.append(";").append(BY_MONTHDAY_PREFIX).append("=").append(rruleDto.dayOfWeek());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(BY_MONTHDAY_PREFIX)) {
            rruleDtoBuilder.dayOfMonth(Integer.valueOf(value));
        }
    }
}
