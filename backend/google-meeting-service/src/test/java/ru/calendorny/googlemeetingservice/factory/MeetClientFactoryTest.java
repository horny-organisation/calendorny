package ru.calendorny.googlemeetingservice.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MeetClientFactoryTest {

    @InjectMocks
    private MeetClientFactory factory;

    @Mock
    private SpacesServiceSettings.Builder settingsBuilder;

    @Mock
    private SpacesServiceSettings settings;

    @Mock
    private SpacesServiceClient client;

    @Mock
    private Credentials credentials;

    @Test
    void testCreateClient() throws IOException {
        try (MockedStatic<SpacesServiceSettings> settingsStatic = mockStatic(SpacesServiceSettings.class);
                MockedStatic<SpacesServiceClient> clientStatic = mockStatic(SpacesServiceClient.class)) {

            settingsStatic.when(SpacesServiceSettings::newBuilder).thenReturn(settingsBuilder);
            when(settingsBuilder.setCredentialsProvider(any())).thenReturn(settingsBuilder);
            when(settingsBuilder.build()).thenReturn(settings);
            clientStatic.when(() -> SpacesServiceClient.create(settings)).thenReturn(client);

            SpacesServiceClient result = factory.create(credentials);

            assertNotNull(result);
            verify(settingsBuilder).setCredentialsProvider(any(FixedCredentialsProvider.class));
            verify(settingsBuilder).build();
        }
    }
}
