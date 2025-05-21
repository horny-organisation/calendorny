package ru.calendorny.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPasswordAttempt, String realHashedPassword) {
        return passwordEncoder.matches(rawPasswordAttempt, realHashedPassword);
    }
}
