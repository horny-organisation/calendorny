package ru.calendorny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.calendorny.rabbit.properties.RabbitConfigProperties;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties({
    RabbitConfigProperties.class
})
public class EventServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }


}
