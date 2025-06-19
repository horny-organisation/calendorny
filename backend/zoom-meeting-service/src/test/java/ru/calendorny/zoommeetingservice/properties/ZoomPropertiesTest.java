package ru.calendorny.zoommeetingservice.properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class ZoomPropertiesTest {
    private static final String PRINCIPAL_NAME = "user@example.com";
    private static final String CLIENT_REGISTRATION_ID = "zoom-client-id";
    private static final String BASE_URL = "https://api.zoom.us/v2";
    private static final String MEETING_CREATE_URL = "/users/me/meetings";
    private static final String TIMEZONE = "UTC";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "app.zoom.principalName=%s".formatted(PRINCIPAL_NAME),
                    "app.zoom.clientRegistrationId=%s".formatted(CLIENT_REGISTRATION_ID),
                    "app.zoom.baseUrl=%s".formatted(BASE_URL),
                    "app.zoom.meetingCreateUrl=%s".formatted(MEETING_CREATE_URL),
                    "app.zoom.timezone=%s".formatted(TIMEZONE),
                    "app.zoom.dateTimePattern=%s".formatted(DATETIME_PATTERN));

    @Test
    void testZoomPropertiesBinding() {
        contextRunner.run(context -> {
            ZoomProperties props = context.getBean(ZoomProperties.class);
            assertThat(props.principalName()).isEqualTo(PRINCIPAL_NAME);
            assertThat(props.clientRegistrationId()).isEqualTo(CLIENT_REGISTRATION_ID);
            assertThat(props.baseUrl()).isEqualTo(BASE_URL);
            assertThat(props.meetingCreateUrl()).isEqualTo(MEETING_CREATE_URL);
            assertThat(props.timezone()).isEqualTo(TIMEZONE);
            assertThat(props.dateTimePattern()).isEqualTo(DATETIME_PATTERN);
        });
    }

    @EnableConfigurationProperties(ZoomProperties.class)
    static class TestConfig {}
}
