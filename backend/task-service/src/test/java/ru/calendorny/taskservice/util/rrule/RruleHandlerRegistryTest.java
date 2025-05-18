package ru.calendorny.taskservice.util.rrule;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.exception.InvalidRruleException;
import ru.calendorny.taskservice.util.rrule.RruleHandler;
import ru.calendorny.taskservice.util.rrule.RruleHandlerRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RruleHandlerRegistryTest {

    @MockitoBean
    private WeeklyRruleHandler weeklyHandler;

    @MockitoBean
    private MonthlyRruleHandler monthlyHandler;

    @Autowired
    private RruleHandlerRegistry registry;

    @BeforeEach
    void beforeEach() {
        List<RruleHandler> handlers = Arrays.asList(weeklyHandler, monthlyHandler);
        registry = new RruleHandlerRegistry(handlers);
    }

    @Test
    void testFindHandlerWithWeeklyFrequency() {
        when(weeklyHandler.supports(RruleDto.Frequency.WEEKLY)).thenReturn(true);
        Optional<RruleHandler> result = registry.findHandler(RruleDto.Frequency.WEEKLY);
        assertTrue(result.isPresent());
        assertEquals(weeklyHandler, result.get());
    }

    @Test
    void testFindHandlerWithMonthlyFrequency() {
        when(monthlyHandler.supports(RruleDto.Frequency.MONTHLY)).thenReturn(true);
        Optional<RruleHandler> result = registry.findHandler(RruleDto.Frequency.MONTHLY);
        assertTrue(result.isPresent());
        assertEquals(monthlyHandler, result.get());
    }

    @Test
    void testFindHandlerWithNullFrequency() {
        Optional<RruleHandler> result = registry.findHandler(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSetKeyValue() {
        RruleDto.RruleDtoBuilder builder = RruleDto.builder();
        String key = "BYDAY";
        String value = "MONDAY";

        registry.setKeyValue(key, value, builder);

        verify(weeklyHandler).setToDto(key, value, builder);
        verify(monthlyHandler).setToDto(key, value, builder);
    }

    @Test
    void testValidate() throws InvalidRruleException {
        when(weeklyHandler.supports(RruleDto.Frequency.WEEKLY)).thenReturn(true);

        RruleDto dto = RruleDto.builder()
            .frequency(RruleDto.Frequency.WEEKLY)
            .build();

        registry.validate(dto);

        verify(weeklyHandler).validate(dto);
        verify(monthlyHandler, never()).validate(any());
    }

    @Test
    void testValidateRruleStringWhenMissingFreqPrefix() {
        String invalidRrule = "BYDAY=MONDAY";

        InvalidRruleException exception = assertThrows(InvalidRruleException.class,
            () -> registry.validateRruleString(invalidRrule));

        assertEquals("RRULE must start with FREQ=", exception.getMessage());
    }

    @Test
    void testValidateRruleStringWithInvalidFrequency() {
        String invalidRrule = "FREQ=INVALID;BYDAY=MONDAY";
        InvalidRruleException exception = assertThrows(InvalidRruleException.class,
            () -> registry.validateRruleString(invalidRrule));
        assertEquals("Invalid frequency in RRULE", exception.getMessage());
    }

    @Test
    void testValidateRruleStringWithValidWeeklyRule() throws InvalidRruleException {
        when(weeklyHandler.supports(RruleDto.Frequency.WEEKLY)).thenReturn(true);
        String validRrule = "FREQ=WEEKLY;BYDAY=MONDAY";
        registry.validateRruleString(validRrule);
        verify(weeklyHandler).validateRruleString(validRrule);
        verify(monthlyHandler, never()).validateRruleString(any());
    }

    @Test
    void testValidateRruleStringWithValidMonthlyRule() throws InvalidRruleException {
        when(monthlyHandler.supports(RruleDto.Frequency.MONTHLY)).thenReturn(true);
        String validRrule = "FREQ=MONTHLY;BYMONTHDAY=15";
        registry.validateRruleString(validRrule);
        verify(monthlyHandler).validateRruleString(validRrule);
        verify(weeklyHandler, never()).validateRruleString(any());
    }
}
