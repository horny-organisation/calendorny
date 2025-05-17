package ru.calendorny.taskservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.RruleDto;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RruleHandlerRegistry {

    private final List<RruleHandler> handlers;

    public Optional<RruleHandler> findHandler(RruleDto.Frequency frequency) {
        return handlers.stream()
            .filter(handler -> handler.supports(frequency))
            .findFirst();
    }

    public void setKeyValue(String key, String value, RruleDto.RruleDtoBuilder rruleDtoBuilder) {
        for (RruleHandler handler : handlers) {
            handler.setToDto(key, value, rruleDtoBuilder);
        }
    }
}
