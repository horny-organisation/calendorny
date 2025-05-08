package ru.calendorny.googlemeetingservice.factory;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.apps.meet.v2.SpacesServiceSettings;
import com.google.auth.Credentials;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetClientFactory {
    public SpacesServiceClient create(Credentials credentials) throws IOException {
        SpacesServiceSettings settings = SpacesServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        return SpacesServiceClient.create(settings);
    }
}
