package ru.calendorny.notificationservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7.2")).withExposedPorts(6379);
    }

    static GenericContainer<?> kafka = new GenericContainer<>(DockerImageName.parse("apache/kafka:4.0.0"))
        .withExposedPorts(9092);

    static {
        kafka.start();
    }
}
