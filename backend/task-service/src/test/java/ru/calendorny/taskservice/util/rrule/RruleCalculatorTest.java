package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.exception.RruleParsingException;
import ru.calendorny.taskservice.util.rrulehandler.RruleHandlerRegistry;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RruleCalculatorTest {

    @MockitoBean
    private RruleHandlerRegistry rruleHandlerRegistry;

    @Autowired
    private RruleCalculator rruleCalculator;

    private static final String CORRECT_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=TU";

    private static final String CORRECT_MONTHLY_RRULE = "FREQ=MONTHLY;BYMONTHDAY=15";

    private static final String INCORRECT_RRULE = "INCORRECT";

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
