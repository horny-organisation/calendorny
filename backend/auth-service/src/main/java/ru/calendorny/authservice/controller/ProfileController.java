package ru.calendorny.authservice.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.authservice.security.AuthenticatedUser;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getInfo() {
        return Map.of("message", "public information");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getProfile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return Map.of(
                "id", authenticatedUser.id().toString(),
                "email", authenticatedUser.email());
    }
}
