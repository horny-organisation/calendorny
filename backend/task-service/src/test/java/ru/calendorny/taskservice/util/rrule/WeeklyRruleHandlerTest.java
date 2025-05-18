package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import java.time.DayOfWeek;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeeklyRruleHandlerTest {

    private static final String BY_DAY_PREFIX = "BYDAY";
    private static final String INVALID_PREFIX = "INVALID";
    private static final String VALID_WEEKLY_RRULE = "FREQ=WEEKLY;BYDAY=MONDAY";

    @Autowired
    private WeeklyRruleHandler handler;

    @Test
    void testSupportsWithWeeklyFrequency() {
        assertTrue(handler.supports(RruleDto.Frequency.WEEKLY));
    }

    @Test
    void testSupportsWithNonWeeklyFrequency() {
        assertFalse(handler.supports(RruleDto.Frequency.MONTHLY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    void testAppendWithValidDay(String day) {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
            .dayOfWeek(DayOfWeek.valueOf(day))
            .build();

        StringBuilder sb = new StringBuilder();
        handler.append(dto, sb);

        assertEquals(";BYDAY=%s".formatted(day), sb.toString());
    }

    @Test
    void testAppendWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
            .dayOfWeek(null)
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> handler.append(dto, new StringBuilder()));
    }

    @Test
    void testSetToDtoCorrect() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        handler.setToDto(BY_DAY_PREFIX, "MONDAY", builder);

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
            .frequency(RruleDto.Frequency.WEEKLY)
            .dayOfWeek(DayOfWeek.valueOf(day))
            .build();

        assertDoesNotThrow(() -> handler.validate(dto));
    }

    @Test
    void testValidateWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
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
