package ru.calendorny.authservice.service;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.calendorny.authservice.dto.request.RegisterRequest;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.dto.response.AuthTokens;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.RefreshToken;
import ru.calendorny.authservice.exception.LoginException;
import ru.calendorny.authservice.exception.RegistrationException;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.RefreshTokenRepository;
import ru.calendorny.authservice.security.JwtService;
import ru.calendorny.authservice.security.PasswordService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountRepository accountRepository;
    private final ProfileService profileService;
    private final PasswordService passwordService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public UUID register(RegisterRequest registerRequest) {
        log.debug("Registering account with email: {}", registerRequest.email());
        if (accountRepository.findByEmail(registerRequest.email()).isPresent()) {
            log.debug("User with email: {} already exists", registerRequest.email());
            throw new RegistrationException("User with this email already exists");
        }

        String hashedPassword = passwordService.hash(registerRequest.password());

        int attempts = 0;
        while (attempts < 5) {
            UUID id = UUID.randomUUID();
            if (accountRepository.findById(id).isEmpty()) {
                Account account = new Account(id, registerRequest.email(), hashedPassword);
                accountRepository.save(account);
                log.info("Registered account with email: {}", registerRequest.email());
                profileService.save(id, UserProfileEdit.builder()
                    .firstName(registerRequest.firstName())
                    .lastName(registerRequest.lastName())
                    .build());
                return id;
            }
            attempts++;
        }

        log.error("User with email: {} not registered - UUID not given", registerRequest.email());
        throw new RegistrationException("Unable to generate unique user ID after several attempts");
    }

    public AuthTokens login(String email, String rawPassword) {
        log.debug("Trying to login with email: {}", email);
        Account account =
            accountRepository.findByEmail(email).orElseThrow(() -> {
                log.debug("Account with email: {} not found", email);
                return new LoginException("Invalid email or password");
            });

        if (!account.isActive()) {
            log.debug("User with email: {} is not active", email);
            throw new LoginException("Account is disabled");
        }

        if (!passwordService.matches(rawPassword, account.getPasswordHash())) {
            log.debug("User with email: {} does not match password", email);
            throw new LoginException("Invalid email or password");
        }
        log.debug("Login successful");
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
        log.debug("Trying to give access token for userId={}", userId);
        Account account = accountRepository.findById(userId).orElseThrow(() -> {
            log.debug("Account with id: {} not found", userId);
            return new LoginException("Account not found");
        });
        return getAuthTokens(account, refreshToken);
    }

    private AuthTokens getAuthTokens(Account account, String refreshToken) {
        log.debug("generating access token for userId={} by refresh", account.getId());
        String accessToken = jwtService.generateAccessToken(
            account.getId().toString(),
            Map.of(
                "email", account.getEmail(),
                "id", account.getId()));

        return new AuthTokens(accessToken, refreshToken);
    }

    private UUID validateRefreshToken(String refreshToken) {
        log.debug("trying to find refresh in db {}", refreshToken);
        RefreshToken token = refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(() -> {
                log.warn("Refresh token not found in database: {}", refreshToken);
                return new LoginException("Invalid refresh token");
            });
        log.debug("Refresh token validated");
        return token.getUserId();
    }

    private AuthTokens getAuthTokens(Account account) {
        log.debug("generating access token for userId={} by login and password", account.getId());
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
