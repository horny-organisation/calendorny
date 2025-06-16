package ru.calendorny.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.calendorny.taskservice.kafka.properties.KafkaConfigProperties;
import ru.calendorny.taskservice.security.JwtProperties;
import ru.calendorny.taskservice.util.logging.properties.LoggingConfigProperties;

@EnableScheduling
@EnableConfigurationProperties({
    KafkaConfigProperties.class,
    LoggingConfigProperties.class,
    JwtProperties.class
})
@SpringBootApplication
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}
