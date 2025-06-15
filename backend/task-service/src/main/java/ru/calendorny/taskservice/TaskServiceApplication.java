package ru.calendorny.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.calendorny.taskservice.kafka.properties.KafkaConfigProperties;
import ru.calendorny.taskservice.security.JwtProperties;

@EnableScheduling
@EnableConfigurationProperties({JwtProperties.class, KafkaConfigProperties.class})
@SpringBootApplication
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}
