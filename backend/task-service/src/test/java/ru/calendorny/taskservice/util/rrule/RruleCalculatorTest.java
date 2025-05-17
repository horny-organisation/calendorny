package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.exception.RruleParsingException;
import ru.calendorny.taskservice.util.rrule.RruleCalculator;
import ru.calendorny.taskservice.util.rrule.RruleHandlerRegistry;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RruleCalculatorTest {

    private static final String CORRECT_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=TU";

    private static final String CORRECT_MONTHLY_RRULE = "FREQ=MONTHLY;BYMONTHDAY=15";

    private static final String INCORRECT_RRULE = "INCORRECT";

    @Mock
    private RruleHandlerRegistry rruleHandlerRegistry;

    @InjectMocks
    private RruleCalculator rruleCalculator;

    @Test
    void testFindNextDateWithCorrectWeeklyRrule() {
        LocalDate fromDate = LocalDate.of(2025, 5, 14);
        LocalDate result = rruleCalculator.findNextDate(CORRECT_WEEKLY_RRULE, fromDate);
        assertEquals(LocalDate.of(2025, 5, 20), result);
    }

    @Test
    void testFindNextDateWithCorrectMonthlyRrule() {
        LocalDate fromDate = LocalDate.of(2025, 5, 1);
        LocalDate result = rruleCalculator.findNextDate(CORRECT_MONTHLY_RRULE, fromDate);
        assertEquals(LocalDate.of(2025, 5, 15), result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void findNextDate_shouldThrowInvalidRruleExceptionWhenRruleIsBlank(String rrule) {
        LocalDate fromDate = LocalDate.now(ZoneOffset.UTC);
        assertThrows(InvalidRruleException.class,
            () -> rruleCalculator.findNextDate(rrule, fromDate));
    }

    @Test
    void findNextDate_shouldThrowIllegalArgumentExceptionWhenFromDateIsNull() {
        String validRrule = "FREQ=WEEKLY;BYDAY=MO";
        assertThrows(IllegalArgumentException.class,
            () -> rruleCalculator.findNextDate(validRrule, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "INVALID",
        "FREQ=UNKNOWN",
        "FREQ=WEEKLY;BYDAY=INVALIDDAY",
        "FREQ=MONTHLY;BYMONTHDAY=35",
        "FREQ=YEARLY",
        "FREQ=WEEKLY",
        "FREQ=MONTHLY",
        "FREQ=WEEKLY;BYMONTHDAY=5",
        "FREQ=MONTHLY;BYDAY=MO"
    })
    void testFindNextDateWithInvalidRules(String invalidRrule) {
        LocalDate fromDate = LocalDate.now(ZoneOffset.UTC);
        doThrow(new InvalidRruleException("Invalid rule"))
            .when(rruleHandlerRegistry).validateRruleString(invalidRrule);
        assertThrows(RruleParsingException.class,
            () -> rruleCalculator.findNextDate(invalidRrule, fromDate));
    }

    @Test
    void testFindNextDateWhenParserFails() {
        LocalDate fromDate = LocalDate.now(ZoneOffset.UTC);
        doNothing().when(rruleHandlerRegistry).validateRruleString(INCORRECT_RRULE);
        assertThrows(RruleParsingException.class,
            () -> rruleCalculator.findNextDate(INCORRECT_RRULE, fromDate));
    }
}
