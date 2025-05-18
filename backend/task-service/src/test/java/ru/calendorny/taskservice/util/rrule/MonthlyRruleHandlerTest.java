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
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MonthlyRruleHandlerTest {

    @Autowired
    private MonthlyRruleHandler handler;

    private static final String BY_MONTHDAY_PREFIX = "BYMONTHDAY";

    private static final String INVALID_PREFIX = "INVALID";

    private static final String VALID_MONTHLY_RRULE = "FREQ=MONTHLY;BYMONTHDAY=1";

    @Test
    void testSupportsWithMonthlyFrequency() {
        assertTrue(handler.supports(RruleDto.Frequency.MONTHLY));
    }

    @Test
    void testSupportsWithNonMonthlyFrequency() {
        assertFalse(handler.supports(RruleDto.Frequency.WEEKLY));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 15, 31})
    void testAppendWithValidDay(int day) {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(day)
            .build();

        StringBuilder sb = new StringBuilder();
        handler.append(dto, sb);

        assertEquals(";BYMONTHDAY=%s".formatted(day), sb.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 32})
    void testAppendWithInvalidDay(int invalidDay) {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(invalidDay)
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> handler.append(dto, new StringBuilder()));
    }

    @Test
    void testAppendWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(null)
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> handler.append(dto, new StringBuilder()));
    }

    @Test
    void testSetToDtoCorrect() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        handler.setToDto(BY_MONTHDAY_PREFIX, "15", builder);

        RruleDto result = builder.build();
        assertEquals(15, result.dayOfMonth());
    }

    @Test
    void testSetToDtoWithInvalidKey() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        handler.setToDto(INVALID_PREFIX, "value", builder);

        RruleDto result = builder.build();
        assertNull(result.dayOfMonth());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 15, 31})
    void testValidateWithValidDay(int day) {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(day)
            .build();

        assertDoesNotThrow(() -> handler.validate(dto));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 32})
    void testValidateWithInvalidDay(int invalidDay) {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(invalidDay)
            .build();

        assertThrows(InvalidRruleException.class, () -> handler.validate(dto));
    }

    @Test
    void testValidateWithNullDay() {
        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.MONTHLY)
            .dayOfMonth(null)
            .build();

        assertThrows(InvalidRruleException.class, () -> handler.validate(dto));
    }

    @Test
    void testValidateRruleStringWithValidRrule() {
        assertDoesNotThrow(() -> handler.validateRruleString(VALID_MONTHLY_RRULE));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidMonthlyRrules")
    void testValidateRruleStringWithInvalidRrule(String invalidRrule, Class<? extends Exception> expectedException, String expectedMessagePart) {
        Exception exception = assertThrows(expectedException,
            () -> handler.validateRruleString(invalidRrule));

        assertNotNull(exception.getMessage(), "Exception message should not be null");
        assertTrue(exception.getMessage().contains(expectedMessagePart),
            "Exception message should contain: " + expectedMessagePart + " but was: " + exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidMonthlyRrules() {
        return Stream.of(
            Arguments.of(
                "FREQ=MONTHLY",
                InvalidRruleException.class,
                "requires BYMONTHDAY parameter"),
            Arguments.of(
                "FREQ=MONTHLY;BYMONTHDAY=32",
                InvalidRruleException.class,
                "must be between 1 and 31"),
            Arguments.of(
                "FREQ=MONTHLY;BYMONTHDAY=0",
                InvalidRruleException.class,
                "must be between 1 and 31"),
            Arguments.of(
                "FREQ=MONTHLY;BYMONTHDAY=invalid",
                InvalidRruleException.class,
                "must be a number"),
            Arguments.of(
                "FREQ=MONTHLY;BYMONTHDAY=",
                InvalidRruleException.class,
                "Invalid BYMONTHDAY format")
        );
    }
}
