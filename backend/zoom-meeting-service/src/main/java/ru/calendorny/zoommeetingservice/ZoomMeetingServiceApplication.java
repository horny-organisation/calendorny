package ru.calendorny.zoommeetingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.calendorny.zoommeetingservice.properties.RabbitProperties;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;

@SpringBootApplication
@EnableConfigurationProperties({RabbitProperties.class, ZoomProperties.class})
public class ZoomMeetingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoomMeetingServiceApplication.class, args);
    }
}
