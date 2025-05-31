package ru.calendorny.googlemeetingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.calendorny.googlemeetingservice.properties.GoogleOauthProperties;
import ru.calendorny.googlemeetingservice.properties.RabbitProperties;

@SpringBootApplication
@EnableConfigurationProperties({RabbitProperties.class, GoogleOauthProperties.class})
public class GoogleMeetingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleMeetingServiceApplication.class, args);
    }
}
