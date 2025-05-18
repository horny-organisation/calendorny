package ru.calendorny.authservice.service;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.calendorny.authservice.dto.response.AuthTokens;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.RefreshToken;
import ru.calendorny.authservice.exception.LoginException;
import ru.calendorny.authservice.exception.UserAlreadyExistException;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.RefreshTokenRepository;
import ru.calendorny.authservice.security.JwtService;
import ru.calendorny.authservice.security.PasswordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordService passwordService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public UUID register(String email, String rawPassword) {
        log.info("Registering account with email: {}", email);
        if (accountRepository.findByEmail(email).isPresent()) {
            log.info("User with email: {} already exists", email);
            throw new UserAlreadyExistException("User with this email already exists");
        }

        String hashedPassword = passwordService.hash(rawPassword);

        int attempts = 0;
        while (attempts < 5) {
            UUID id = UUID.randomUUID();
            if (accountRepository.findById(id).isEmpty()) {
                Account account = new Account(id, email, hashedPassword);
                accountRepository.save(account);
                log.info("Registered account with email: {}", email);
                return id;
            }
            attempts++;
        }

        log.error("User with email: {} not registered - UUID not given", email);
        throw new IllegalStateException("Unable to generate unique user ID after several attempts");
    }

    public AuthTokens login(String email, String rawPassword) {
        Account account =
                accountRepository.findByEmail(email).orElseThrow(() -> new LoginException("Invalid email or password"));

        if (!account.isActive()) {
            throw new LoginException("Account is disabled");
        }

        if (!passwordService.matches(rawPassword, account.getPasswordHash())) {
            throw new LoginException("Invalid email or password");
        }

        return getAuthTokens(account);
    }

    public AuthTokens refresh(String refreshToken) {
        UUID userId = validateRefreshToken(refreshToken);
        return updateAccessTokenByRefreshToken(userId, refreshToken);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private AuthTokens updateAccessTokenByRefreshToken(UUID userId, String refreshToken) {
        Account account = accountRepository.findById(userId).orElseThrow(() -> new LoginException("Account not found"));
        return getAuthTokens(account, refreshToken);
    }

    private AuthTokens getAuthTokens(Account account, String refreshToken) {
        String accessToken = jwtService.generateAccessToken(
                account.getId().toString(),
                Map.of(
                        "email", account.getEmail(),
                        "id", account.getId()));

        return new AuthTokens(accessToken, refreshToken);
    }

    private UUID validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new LoginException("Invalid refresh token"));
        return token.getUserId();
    }

    private AuthTokens getAuthTokens(Account account) {
        String accessToken = jwtService.generateAccessToken(
                account.getId().toString(),
                Map.of(
                        "email", account.getEmail(),
                        "id", account.getId()));
        String refreshToken = jwtService.generateRefreshToken(account.getId().toString());

        refreshTokenRepository.save(new RefreshToken(refreshToken, account.getId()));

        return new AuthTokens(accessToken, refreshToken);
    }
}
