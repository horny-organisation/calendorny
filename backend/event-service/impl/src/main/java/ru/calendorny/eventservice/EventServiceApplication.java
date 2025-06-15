package ru.calendorny.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.calendorny.eventservice.kafka.properties.KafkaConfigProperties;
import ru.calendorny.eventservice.rabbit.properties.RabbitConfigProperties;
import ru.calendorny.securitystarter.SecurityAutoConfiguration;

@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties({
    RabbitConfigProperties.class,
    KafkaConfigProperties.class
})
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class EventServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }


}
