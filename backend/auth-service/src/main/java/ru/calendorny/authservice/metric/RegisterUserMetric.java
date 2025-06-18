package ru.calendorny.authservice.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserMetric {

    private final Counter registeredUsersCounter;

    public RegisterUserMetric(MeterRegistry registry) {
        this.registeredUsersCounter = Counter.builder("registered_users_total")
            .description("Total number of registered users")
            .register(registry);
    }

    public void incrementRegisteredUsers() {
        registeredUsersCounter.increment();
    }
}
