package ru.calendorny.taskservice.util.rrule;

import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import java.time.DayOfWeek;

@Component
public class WeeklyRruleHandler implements RruleHandler {

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

    @Override
    public void validate(RruleDto rruleDto) throws InvalidRruleException {
        if (rruleDto.dayOfWeek() == null) {
            throw new InvalidRruleException("WEEKLY frequency requires dayOfWeek");
        }
    }

    @Override
    public void validateRruleString(String rruleString) throws InvalidRruleException {
        if (!rruleString.contains("BYDAY=")) {
            throw new InvalidRruleException("WEEKLY frequency requires BYDAY parameter");
        }

        try {
            String dayPart = rruleString.split("BYDAY=")[1].split(";")[0];
            DayOfWeek unused = DayOfWeek.valueOf(dayPart);
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid BYDAY value in WEEKLY rule");
        }
    }
}
