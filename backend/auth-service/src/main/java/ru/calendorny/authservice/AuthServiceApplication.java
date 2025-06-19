package ru.calendorny.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.calendorny.authservice.config.BotProperties;
import ru.calendorny.authservice.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, BotProperties.class})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
