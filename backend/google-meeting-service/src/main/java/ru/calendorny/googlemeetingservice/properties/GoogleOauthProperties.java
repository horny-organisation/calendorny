package ru.calendorny.googlemeetingservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.google.oauth")
public record GoogleOauthProperties(String fileName, String principalName, String clientRegistrationId) {}
