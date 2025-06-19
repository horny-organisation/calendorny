package ru.calendorny.taskservice.util.logging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging")
public record LoggingConfigProperties(
        boolean enabled,
        boolean debug
) {
}

