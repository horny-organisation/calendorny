package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RruleConverterTest {

    @MockitoBean
    private RruleHandlerRegistry rruleHandlerRegistry;

    @MockitoBean
    private WeeklyRruleHandler weeklyHandler;

    @Autowired
    private RruleConverter rruleConverter;

    private static final String CORRECT_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=MONDAY";

    private static final String CORRECT_MONTHLY_RRULE = "FREQ=MONTHLY;BYMONTHDAY=15";

    private static final String INCORRECT_RRULE = "FREQ=WEEKLY;INVALID_PART";

    @Test
    void testToRruleStringWhenDtoIsNull() {
        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleString(null),
            "RRULE cannot be null");
    }

    @Test
    void testToRruleStringValidatesDto() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
            .build();
        rruleConverter.toRruleString(dto);
        verify(rruleHandlerRegistry).validate(dto);
    }

    @Test
    void testToRruleStringGenerates() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
            .build();

        when(rruleHandlerRegistry.findHandler(RruleDto.Frequency.WEEKLY))
            .thenReturn(Optional.of(weeklyHandler));
        doNothing().when(weeklyHandler).append(eq(dto), any(StringBuilder.class));

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
        rruleConverter.toRruleDto(CORRECT_WEEKLY_RRULE);
        verify(rruleHandlerRegistry).validateRruleString(CORRECT_WEEKLY_RRULE);
    }

    @Test
    void testToRruleDtoParsingFrequency() {
        RruleDto result = rruleConverter.toRruleDto(CORRECT_MONTHLY_RRULE);
        assertEquals(RruleDto.Frequency.MONTHLY, result.frequency());
    }

    @Test
    void testToRruleDtoCallsSetKeyValueForNonFrequencyParts() {
        rruleConverter.toRruleDto(CORRECT_WEEKLY_RRULE);
        verify(rruleHandlerRegistry).setKeyValue(eq("BYDAY"), eq("MONDAY"), any());
    }

    @Test
    void testToRruleDtoValidatesResult() {
        rruleConverter.toRruleDto(CORRECT_MONTHLY_RRULE);
        verify(rruleHandlerRegistry).validate(any(RruleDto.class));
    }

    @Test
    void testToRruleDtoInvalidFormat() {
        assertThrows(InvalidRruleException.class,
            () -> rruleConverter.toRruleDto(INCORRECT_RRULE),
            "Invalid RRULE part");
    }
}
