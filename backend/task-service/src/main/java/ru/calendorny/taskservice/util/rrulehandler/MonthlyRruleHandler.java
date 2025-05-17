package ru.calendorny.taskservice.util.rrulehandler;

import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;

@Component
public class MonthlyRruleHandler implements RruleHandler {

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

    @Override
    public void validate(RruleDto rruleDto) throws InvalidRruleException {
        Integer day = rruleDto.dayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new InvalidRruleException("MONTHLY frequency requires valid dayOfMonth (1-31)");
        }
    }

    @Override
    public void validateRruleString(String rruleString) throws InvalidRruleException {
        if (!rruleString.contains("BYMONTHDAY=")) {
            throw new InvalidRruleException("MONTHLY frequency requires BYMONTHDAY parameter");
        }

        try {
            String dayPart = rruleString.split("BYMONTHDAY=")[1].split(";")[0];
            int day = Integer.parseInt(dayPart);
            if (day < 1 || day > 31) {
                throw new InvalidRruleException("BYMONTHDAY must be between 1 and 31");
            }
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid BYMONTHDAY value in MONTHLY rule");
        }
    }
}
