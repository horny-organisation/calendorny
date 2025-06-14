package ru.calendorny.eventservice.util.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    //TODO: точные endpoint-ы у auth-service

    @GetMapping("/api/v1/auth/users")
    UUID getUserIdByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/auth/users/{userId}")
    String getEmailByUserId(@PathVariable("userId") UUID userId);
}
