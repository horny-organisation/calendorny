package ru.calendorny.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.authservice.config.JwtProperties;
import ru.calendorny.authservice.dto.request.LoginRequest;
import ru.calendorny.authservice.dto.request.RegisterRequest;
import ru.calendorny.authservice.dto.response.AuthTokens;
import ru.calendorny.authservice.service.AuthService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody @Valid RegisterRequest request) {
        return Map.of(
                "userId",
                authService.register(request.email(), request.password()).toString());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthTokens login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthTokens tokens = authService.login(request.email(), request.password());

        String cookieValue = "refreshToken=%s; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=%d"
                .formatted(tokens.refreshToken(), jwtProperties.refreshTokenExpirationDays() * 24L * 60L * 60L);
        response.addHeader("Set-Cookie", cookieValue);

        return new AuthTokens(tokens.accessToken(), null);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthTokens refresh(@CookieValue("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);
        String expiredCookie = "refreshToken=; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=0";
        response.addHeader("Set-Cookie", expiredCookie);
    }
}
