package ru.calendorny.taskservice.util.rrulehandler;

import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import java.time.DayOfWeek;

import static ru.calendorny.taskservice.util.constant.RruleConstants.*;

@Component
public class WeeklyRruleHandler implements RruleHandler {

    @Override
    public boolean supports(TaskFrequency frequency) {
        return frequency == TaskFrequency.WEEKLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        if (rruleDto.dayOfWeek() == null) {
            throw new IllegalArgumentException("WEEKLY requires dayOfWeek");
        }
        sb.append(";").append(BY_DAY_PREFIX).append(rruleDto.dayOfWeek());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(BY_DAY_KEY)) {
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
        if (!rruleString.contains(BY_DAY_PREFIX)) {
            throw new InvalidRruleException("WEEKLY frequency requires BYDAY parameter");
        }

        try {
            String dayPart = rruleString.split(BY_DAY_PREFIX)[1].split(";")[0];
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayPart);
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid BYDAY value in WEEKLY rule");
        }
    }
}
