package ru.calendorny.authservice;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    Network network() {
        return Network.newNetwork();
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer(Network network) {
        return new PostgreSQLContainer<>("postgres:16-alpine")
            .withExposedPorts(5432)
            .withDatabaseName("local")
            .withUsername("postgres")
            .withPassword("test")
            .withNetwork(network)
            .withNetworkAliases("postgres");
    }

    @Bean
    GenericContainer<?> liquibaseContainer(PostgreSQLContainer<?> postgresContainer, Network network) {
        File migrationsDir = new File("./migrations");
        String migrationsPath = null;
        try {
            migrationsPath = migrationsDir.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("liquibase/liquibase:4.29"))
            .withNetwork(network)
            .withFileSystemBind(migrationsPath, "/changesets", BindMode.READ_WRITE)
            .withCommand(
                "--searchPath=/changesets",
                "--changelog-file=changelog-master.yaml",
                "--driver=org.postgresql.Driver",
                "--url=jdbc:postgresql://postgres:5432/local",
                "--username=" + postgresContainer.getUsername(),
                "--password=" + postgresContainer.getPassword(),
                "update")
            .waitingFor(Wait.forLogMessage(".*Liquibase command 'update' was executed successfully.*", 1))
            .withStartupTimeout(Duration.ofMinutes(2))
            .dependsOn(postgresContainer);

        return container;
    }

}
