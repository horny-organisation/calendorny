package ru.calendorny.googlemeetingservice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login/success")
    public String success(@AuthenticationPrincipal OidcUser oidcUser) {
        // sub — это уникальный идентификатор пользователя
        String sub = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        return "Authenticated as: " + sub + " (email: " + email + ")";
    }
}
