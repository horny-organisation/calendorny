package ru.calendorny.googlemeetingservice.properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class GoogleOauthPropertiesTest {
    private static final String PRINCIPAL_NAME = "user@example.com";
    private static final String CLIENT_REGISTRATION_ID = "google-client-id";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "app.google.oauth.principalName=%s".formatted(PRINCIPAL_NAME),
                    "app.google.oauth.clientRegistrationId=%s".formatted(CLIENT_REGISTRATION_ID));

    @Test
    void testGoogleOauthPropertiesBinding() {
        contextRunner.run(context -> {
            GoogleOauthProperties props = context.getBean(GoogleOauthProperties.class);
            assertThat(props.principalName()).isEqualTo(PRINCIPAL_NAME);
            assertThat(props.clientRegistrationId()).isEqualTo(CLIENT_REGISTRATION_ID);
        });
    }

    @EnableConfigurationProperties(GoogleOauthProperties.class)
    static class TestConfig {}
}
