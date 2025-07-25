package ru.calendorny.taskservice.util.rrulehandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.exception.InvalidRruleException;

import java.time.DayOfWeek;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.calendorny.taskservice.util.constant.RruleConstants.*;

public class WeeklyRruleHandlerTest {

    private WeeklyRruleHandler handler;

    private static final String INVALID_PREFIX = "INVALID";

    private static final String VALID_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=MONDAY";

    @BeforeEach
    void setUp() {
        handler = new WeeklyRruleHandler();
    }

    @Test
    void testSupportsWithWeeklyFrequency() {
        assertTrue(handler.supports(TaskFrequency.WEEKLY));
    }

    @Test
    void testSupportsWithNonWeeklyFrequency() {
        assertFalse(handler.supports(TaskFrequency.MONTHLY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    void testAppendWithValidDay(String day) {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(DayOfWeek.valueOf(day))
            .build();

        StringBuilder sb = new StringBuilder();
        handler.append(dto, sb);

        assertEquals(";BYDAY=%s".formatted(day), sb.toString());
    }

    @Test
    void testAppendWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(null)
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> handler.append(dto, new StringBuilder()));
    }

    @Test
    void testSetToDtoCorrect() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        handler.setToDto(BY_DAY_KEY, "MONDAY", builder);

        RruleDto result = builder.build();
        assertEquals(DayOfWeek.MONDAY, result.dayOfWeek());
    }

    @Test
    void testSetToDtoWithInvalidKey() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        handler.setToDto(INVALID_PREFIX, "value", builder);

        RruleDto result = builder.build();
        assertNull(result.dayOfWeek());
    }

    @ParameterizedTest
    @ValueSource(strings = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    void testValidateWithValidDay(String day) {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(DayOfWeek.valueOf(day))
            .build();

        assertDoesNotThrow(() -> handler.validate(dto));
    }

    @Test
    void testValidateWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(null)
            .build();

        assertThrows(InvalidRruleException.class, () -> handler.validate(dto));
    }

    @Test
    void testValidateRruleStringWithValidRrule() {
        assertDoesNotThrow(() -> handler.validateRruleString(VALID_WEEKLY_RRULE));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidWeeklyRrules")
    void testValidateRruleStringWithInvalidRrule(String invalidRrule, Class<? extends Exception> expectedException, String expectedMessagePart) {
        Exception exception = assertThrows(expectedException,
            () -> handler.validateRruleString(invalidRrule));

        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(exception.getMessage().contains(expectedMessagePart),
            "Exception message should contain: " + expectedMessagePart + " but was: " + exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidWeeklyRrules() {
        return Stream.of(
            Arguments.of(
                "FREQ=WEEKLY",
                InvalidRruleException.class,
                "requires BYDAY parameter"),
            Arguments.of(
                "FREQ=WEEKLY;BYDAY=INVALID_DAY",
                InvalidRruleException.class,
                "Invalid BYDAY value"),
            Arguments.of(
                "FREQ=WEEKLY;BYDAY=",
                InvalidRruleException.class,
                "Invalid BYDAY value")
        );
    }
}
