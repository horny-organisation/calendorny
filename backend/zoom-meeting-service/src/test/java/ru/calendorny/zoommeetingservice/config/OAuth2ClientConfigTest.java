package ru.calendorny.zoommeetingservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.reactive.function.client.WebClient;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;

@ExtendWith(MockitoExtension.class)
public class OAuth2ClientConfigTest {

    private static final String CLIENT_REGISTRATION_ID = "zoom";
    private static final String PRINCIPAL_NAME = "user";
    private static final String ACCESS_TOKEN = "mockAccessToken";
    private static final String BASE_URL = "https://api.zoom.us/";

    @Mock
    private ZoomProperties properties;

    @InjectMocks
    private OAuth2ClientConfig oAuth2ClientConfig;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private JdbcOperations jdbcOperations;

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Test
    void testAuthorizedClientService() {
        lenient().when(properties.clientRegistrationId()).thenReturn(CLIENT_REGISTRATION_ID);
        OAuth2AuthorizedClientService clientService =
                oAuth2ClientConfig.authorizedClientService(jdbcOperations, clientRegistrationRepository);
        assertThat(clientService).isInstanceOf(JdbcOAuth2AuthorizedClientService.class);
    }

    @Test
    void testAuthorizedClientManager() {
        OAuth2AuthorizedClientManager clientManager =
                oAuth2ClientConfig.authorizedClientManager(clientRegistrationRepository, authorizedClientService);
        assertThat(clientManager).isNotNull();
    }

    @Test
    void testAuthorizedClientThrowsExceptionIfNoToken() {
        when(properties.clientRegistrationId()).thenReturn(CLIENT_REGISTRATION_ID);
        when(properties.principalName()).thenReturn(PRINCIPAL_NAME);

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> oAuth2ClientConfig.zoomAuthorizedClient(authorizedClientManager));

        assertThat(exception.getMessage()).isEqualTo("Failed to authorize Zoom client");
    }

    @Test
    void testAuthorizedClientSuccessful() {
        OAuth2AccessToken mockAccessToken = mock(OAuth2AccessToken.class);
        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(mockAccessToken);

        when(properties.clientRegistrationId()).thenReturn(CLIENT_REGISTRATION_ID);
        when(properties.principalName()).thenReturn(PRINCIPAL_NAME);

        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(mockClient);

        OAuth2AuthorizedClient client = oAuth2ClientConfig.zoomAuthorizedClient(authorizedClientManager);

        assertThat(client).isNotNull();
        assertThat(client.getAccessToken()).isEqualTo(mockAccessToken);
    }

    @Test
    void testZoomWebClient() {
        OAuth2AccessToken mockAccessToken = mock(OAuth2AccessToken.class);
        OAuth2AuthorizedClient mockClient = mock(OAuth2AuthorizedClient.class);
        when(mockClient.getAccessToken()).thenReturn(mockAccessToken);
        when(mockAccessToken.getTokenValue()).thenReturn(ACCESS_TOKEN);

        when(properties.baseUrl()).thenReturn(BASE_URL);

        WebClient webClient = oAuth2ClientConfig.zoomWebClient(mockClient);

        assertThat(webClient).isNotNull();
    }
}
