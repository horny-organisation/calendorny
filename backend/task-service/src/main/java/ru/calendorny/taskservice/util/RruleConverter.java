package ru.calendorny.taskservice.util;

import lombok.experimental.UtilityClass;
import java.time.DayOfWeek;

@UtilityClass
public class RruleConverter {

    public static final String FREQUENCY_PERFIX = "FREQ";

    public static final String BY_DAY_PREFIX = "BYDAY";

    public static final String BY_MONTH_PREFIX = "BYMONTH";

    public String toRruleString(RruleDto rruleDto) {
        StringBuilder stringBuilder = new StringBuilder();

        RruleDto.Frequency frequency = rruleDto.getFrequency();

        stringBuilder.append(FREQUENCY_PERFIX).append("=").append(frequency).append(";");

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
                case FREQUENCY_PERFIX -> rruleDto.setFrequency(RruleDto.Frequency.valueOf(kv[1]));
                case BY_DAY_PREFIX -> rruleDto.setDayOfWeek(DayOfWeek.valueOf(kv[1]));
                case BY_MONTH_PREFIX -> rruleDto.setDayOfMonth(Integer.parseInt(kv[1]));
            }
        }
        return rruleDto;
    }
}
