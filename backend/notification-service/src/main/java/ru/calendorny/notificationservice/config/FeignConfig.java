package ru.calendorny.notificationservice.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.calendorny.notificationservice.client.BotAuthFeignErrorDecoder;

@Configuration
public class FeignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new BotAuthFeignErrorDecoder();
    }
}
