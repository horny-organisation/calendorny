package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.util.rrulehandler.RruleHandlerRegistry;
import ru.calendorny.taskservice.util.rrulehandler.WeeklyRruleHandler;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RruleConverterTest {

    private RruleHandlerRegistry rruleHandlerRegistry;
    private WeeklyRruleHandler weeklyHandler;
    private RruleConverter rruleConverter;

    private static final String CORRECT_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=MONDAY";
    private static final String CORRECT_MONTHLY_RRULE = "FREQ=MONTHLY;BYMONTHDAY=15";
    private static final String INCORRECT_RRULE = "FREQ=WEEKLY;INVALID_PART";

    @BeforeEach
    void setUp() {
        rruleHandlerRegistry = mock(RruleHandlerRegistry.class);
        weeklyHandler = mock(WeeklyRruleHandler.class);
        rruleConverter = new RruleConverter(rruleHandlerRegistry);
    }

    @Test
    void testToRruleStringWhenDtoIsNull() {
        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleString(null),
            "RRULE cannot be null");
    }

    @Test
    void testToRruleStringValidatesDto() {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .build();

        when(rruleHandlerRegistry.findHandler(TaskFrequency.WEEKLY))
            .thenReturn(Optional.of(weeklyHandler));
        doNothing().when(rruleHandlerRegistry).validate(dto);
        doNothing().when(weeklyHandler).append(eq(dto), any(StringBuilder.class));

        rruleConverter.toRruleString(dto);
        verify(rruleHandlerRegistry).validate(dto);
    }

    @Test
    void testToRruleStringGenerates() {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .build();

        when(rruleHandlerRegistry.findHandler(TaskFrequency.WEEKLY))
            .thenReturn(Optional.of(weeklyHandler));
        doNothing().when(weeklyHandler).append(eq(dto), any(StringBuilder.class));
        doNothing().when(rruleHandlerRegistry).validate(dto);

        String result = rruleConverter.toRruleString(dto);

        assertTrue(result.startsWith("FREQ=WEEKLY"));
        verify(weeklyHandler).append(eq(dto), any(StringBuilder.class));
    }

    @Test
    void testToRruleDtoWhenStringIsNull() {
        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleDto(null),
            "RRULE string cannot be null or empty");
    }

    @Test
    void testToRruleDtoWhenStringIsBlank() {
        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleDto("   "),
            "RRULE string cannot be null or empty");
    }

    @Test
    void testToRruleDtoValidatesRruleString() {
        doNothing().when(rruleHandlerRegistry).validateRruleString(CORRECT_WEEKLY_RRULE);
        doNothing().when(rruleHandlerRegistry).setKeyValue(eq("BYDAY"), eq("MONDAY"), any());
        doNothing().when(rruleHandlerRegistry).validate(any(RruleDto.class));

        rruleConverter.toRruleDto(CORRECT_WEEKLY_RRULE);
        verify(rruleHandlerRegistry).validateRruleString(CORRECT_WEEKLY_RRULE);
    }

    @Test
    void testToRruleDtoParsingFrequency() {
        doNothing().when(rruleHandlerRegistry).validateRruleString(CORRECT_MONTHLY_RRULE);
        doNothing().when(rruleHandlerRegistry).setKeyValue(anyString(), anyString(), any());
        doNothing().when(rruleHandlerRegistry).validate(any(RruleDto.class));

        RruleDto result = rruleConverter.toRruleDto(CORRECT_MONTHLY_RRULE);
        assertEquals(TaskFrequency.MONTHLY, result.frequency());
    }

    @Test
    void testToRruleDtoCallsSetKeyValueForNonFrequencyParts() {
        doNothing().when(rruleHandlerRegistry).validateRruleString(CORRECT_WEEKLY_RRULE);
        doNothing().when(rruleHandlerRegistry).setKeyValue(eq("BYDAY"), eq("MONDAY"), any());
        doNothing().when(rruleHandlerRegistry).validate(any(RruleDto.class));

        rruleConverter.toRruleDto(CORRECT_WEEKLY_RRULE);
        verify(rruleHandlerRegistry).setKeyValue(eq("BYDAY"), eq("MONDAY"), any());
    }

    @Test
    void testToRruleDtoValidatesResult() {
        doNothing().when(rruleHandlerRegistry).validateRruleString(CORRECT_MONTHLY_RRULE);
        doNothing().when(rruleHandlerRegistry).setKeyValue(anyString(), anyString(), any());
        doNothing().when(rruleHandlerRegistry).validate(any(RruleDto.class));

        rruleConverter.toRruleDto(CORRECT_MONTHLY_RRULE);
        verify(rruleHandlerRegistry).validate(any(RruleDto.class));
    }

    @Test
    void testToRruleDtoInvalidFormat() {
        doNothing().when(rruleHandlerRegistry).validateRruleString(INCORRECT_RRULE);
        doThrow(new InvalidRruleException("Invalid part"))
            .when(rruleHandlerRegistry).setKeyValue(anyString(), anyString(), any());

        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleDto(INCORRECT_RRULE),
            "Invalid RRULE part");
    }
}
