package ru.calendorny.zoommeetingservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    public RabbitMQContainer postgresContainer() {
        return new RabbitMQContainer("rabbitmq:3-management-alpine")
                .withExposedPorts(5672, 15672)
                .withUser("guest", "guest");
    }
}
