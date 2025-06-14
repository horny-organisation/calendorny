package ru.calendorny.eventservice.util.rrule;

import org.springframework.stereotype.Component;
import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.EventFrequency;
import ru.calendorny.eventservice.exception.InvalidRruleException;
import static ru.calendorny.eventservice.util.rrule.RruleConstants.BY_MONTHDAY_KEY;
import static ru.calendorny.eventservice.util.rrule.RruleConstants.BY_MONTHDAY_PREFIX;

@Component
public class MonthlyRruleHandler implements RruleHandler {

    @Override
    public boolean supports(EventFrequency frequency) {
        return frequency == EventFrequency.MONTHLY;
    }

    @Override
    public void append(RruleDto rruleDto, StringBuilder sb) {
        Integer day = rruleDto.dayOfMonth();
        if (day == null || day < 1 || day > 31) {
            throw new IllegalArgumentException("MONTHLY requires dayOfMonth");
        }
        sb.append(";").append(BY_MONTHDAY_PREFIX).append(rruleDto.dayOfMonth());
    }

    @Override
    public void setToDto(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        if (key.equals(BY_MONTHDAY_KEY)) {
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
        if (!rruleString.contains(BY_MONTHDAY_PREFIX)) {
            throw new InvalidRruleException("MONTHLY frequency requires BYMONTHDAY parameter");
        }
        try {
            String dayPart = rruleString.split(BY_MONTHDAY_PREFIX)[1].split(";")[0];
            int day = Integer.parseInt(dayPart);
            if (day < 1 || day > 31) {
                throw new InvalidRruleException("BYMONTHDAY must be between 1 and 31");
            }
        } catch (NumberFormatException e) {
            throw new InvalidRruleException("Invalid BYMONTHDAY value - must be a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidRruleException("Invalid BYMONTHDAY format");
        } catch (Exception e) {
            throw new InvalidRruleException("Invalid RRULE format: " + e.getMessage());
        }
    }
}
