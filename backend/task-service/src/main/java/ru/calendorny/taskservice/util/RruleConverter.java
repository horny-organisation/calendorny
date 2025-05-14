package ru.calendorny.taskservice.util;

import java.time.DayOfWeek;
import lombok.experimental.UtilityClass;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;

@UtilityClass
public class RruleConverter {

    public static final String FREQUENCY_PREFIX = "FREQ";

    public static final String BY_DAY_PREFIX = "BYDAY";

    public static final String BY_MONTH_PREFIX = "BYMONTH";

    public String toRruleString(RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new InvalidRruleException("Rrule can not be null");
        }
        StringBuilder stringBuilder = new StringBuilder();

        RruleDto.Frequency frequency = rruleDto.getFrequency();

        stringBuilder.append(FREQUENCY_PREFIX).append("=").append(frequency).append(";");

        if (frequency.equals(RruleDto.Frequency.WEEKLY)) {
            stringBuilder.append(BY_DAY_PREFIX).append("=").append(rruleDto.getDayOfWeek());
        } else {
            stringBuilder.append(BY_MONTH_PREFIX).append("=").append(rruleDto.getDayOfMonth());
        }

        return stringBuilder.toString();
    }

    public RruleDto toRruleDto(String rruleString) {

        RruleDto rruleDto = new RruleDto();

        String[] parts = rruleString.split(";");

        for (String part : parts) {
            String[] kv = part.split("=");
            switch (kv[0]) {
                case FREQUENCY_PREFIX -> rruleDto.setFrequency(RruleDto.Frequency.valueOf(kv[1]));
                case BY_DAY_PREFIX -> rruleDto.setDayOfWeek(DayOfWeek.valueOf(kv[1]));
                case BY_MONTH_PREFIX -> rruleDto.setDayOfMonth(Integer.parseInt(kv[1]));
                default -> throw new InvalidRruleException("Can not parse RRULE with key: %s".formatted(kv[0]));
            }
        }
        return rruleDto;
    }
}
