package ru.calendorny.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.calendorny.notificationservice.config.BotConfig;
import ru.calendorny.notificationservice.config.BotKafkaProperties;

@EnableConfigurationProperties({BotConfig.class, BotKafkaProperties.class})
@EnableFeignClients
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
