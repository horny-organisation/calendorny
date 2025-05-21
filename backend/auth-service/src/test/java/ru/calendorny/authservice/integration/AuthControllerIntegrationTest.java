package ru.calendorny.authservice.integration;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.calendorny.authservice.TestcontainersConfiguration;
import ru.calendorny.authservice.dto.request.LoginRequest;
import ru.calendorny.authservice.dto.request.RegisterRequest;
import ru.calendorny.authservice.dto.response.AuthTokens;
import ru.calendorny.authservice.dto.response.ValidationErrorResponse;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.ProfileRepository;
import ru.calendorny.authservice.repository.RefreshTokenRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeAll
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE refresh_tokens, profiles, accounts RESTART IDENTITY CASCADE");
    }

    @Test
    @Order(1)
    void registerUserWithNamesShouldRegisterCorrectly() {
        RegisterRequest req = new RegisterRequest("ex@ya.ru", "12345678", "Alex", "Albon");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/register",
            req, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Account> accountOpt = accountRepository.findByEmail("ex@ya.ru");
        assertTrue(accountOpt.isPresent());
        Account account = accountOpt.get();
        assertEquals("ex@ya.ru", account.getEmail());

        Optional<Profile> profileOpt = profileRepository.findByUserId(account.getId());
        assertTrue(profileOpt.isPresent());
        Profile profile = profileOpt.get();
        assertEquals("Alex", profile.getFirstName());
        assertEquals("Albon", profile.getLastName());
    }

    @Test
    @Order(1)
    void registerUserWithNoNamesShouldRegisterCorrectly() {
        RegisterRequest req = new RegisterRequest("ex2@ya.ru", "12345678", null, null);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/register",
            req, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Account> accountOpt = accountRepository.findByEmail("ex2@ya.ru");
        assertTrue(accountOpt.isPresent());
        Account account = accountOpt.get();
        assertEquals("ex2@ya.ru", account.getEmail());

        Optional<Profile> profileOpt = profileRepository.findByUserId(account.getId());
        assertTrue(profileOpt.isPresent());
        Profile profile = profileOpt.get();
        assertNull(profile.getFirstName());
        assertNull(profile.getLastName());
    }

    @Test
    @Order(2)
    void registerUserWithWrongFieldsShouldFail() {
        RegisterRequest req = new RegisterRequest("ex2gfsda.ru", "87654", "t", null);

        ResponseEntity<ValidationErrorResponse> response = restTemplate.postForEntity("/api/v1/register",
            req, ValidationErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().code());
        List<ValidationErrorResponse.ValidationError> expectedErrors = List.of(
            new ValidationErrorResponse.ValidationError("email", "incorrect email"),
            new ValidationErrorResponse.ValidationError("password", "min pass length is 8"),
            new ValidationErrorResponse.ValidationError("firstName", "min firstName length is 2, max is 100")
        );
        assertTrue(expectedErrors.containsAll(response.getBody().validationErrors()));
    }

    @Test
    @Order(2)
    void registerUserWithExistedEmailShouldFail() {
        RegisterRequest req = new RegisterRequest("ex2@ya.ru", "87654321", "test", null);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/register",
            req, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void loginWithCorrectCredentialsShouldSuccess() {
        LoginRequest loginRequest = new LoginRequest("ex@ya.ru", "12345678");

        ResponseEntity<AuthTokens> response = restTemplate.postForEntity("/api/v1/login", loginRequest, AuthTokens.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().accessToken().isEmpty());
    }

    @Test
    @Order(3)
    void loginWithIncorrectCredentialsShouldFail() {
        LoginRequest loginRequest = new LoginRequest("ex@ya.ru", "incorrect password");

        ResponseEntity<AuthTokens> response = restTemplate.postForEntity("/api/v1/login", loginRequest, AuthTokens.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody().accessToken());
        assertNull(response.getBody().refreshToken());
    }

    @Test
    @Order(4)
    void logoutWithCorrectAccessTokenShouldSuccess() {
        LoginRequest loginRequest = new LoginRequest("ex@ya.ru", "12345678");
        ResponseEntity<AuthTokens> loginResponse = restTemplate.postForEntity("/api/v1/login", loginRequest, AuthTokens.class);

        List<String> setCookieHeaders = loginResponse.getHeaders().get("Set-Cookie");
        assertNotNull(setCookieHeaders);
        assertFalse(setCookieHeaders.isEmpty());

        String refreshTokenCookie = setCookieHeaders.stream()
            .filter(cookie -> cookie.startsWith("refreshToken="))
            .findFirst()
            .orElseThrow();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", refreshTokenCookie);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> logoutResponse = restTemplate.postForEntity("/api/v1/logout", requestEntity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
        assertTrue(refreshTokenRepository.findByToken(refreshTokenCookie.substring(13)).isEmpty());
    }

    @Test
    @Order(4)
    void refreshWithCorrectRefreshTokenShouldReturnNewAccessToken() {
        LoginRequest loginRequest = new LoginRequest("ex@ya.ru", "12345678");
        ResponseEntity<AuthTokens> loginResponse = restTemplate.postForEntity("/api/v1/login", loginRequest, AuthTokens.class);

        List<String> setCookieHeaders = loginResponse.getHeaders().get("Set-Cookie");
        assertNotNull(setCookieHeaders);
        assertFalse(setCookieHeaders.isEmpty());

        String refreshTokenCookie = setCookieHeaders.stream()
            .filter(cookie -> cookie.startsWith("refreshToken="))
            .findFirst()
            .orElseThrow();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", refreshTokenCookie);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<AuthTokens> refreshResponse = restTemplate.postForEntity("/api/v1/refresh", requestEntity, AuthTokens.class);

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody());
        assertNotNull(refreshResponse.getBody().accessToken());
        assertFalse(refreshResponse.getBody().accessToken().isEmpty());
    }

    @Test
    @Order(4)
    void refreshWithFakeRefreshTokenShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "refreshToken=fake-refresh-token");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<AuthTokens> refreshResponse = restTemplate.postForEntity("/api/v1/refresh", requestEntity, AuthTokens.class);

        assertEquals(HttpStatus.UNAUTHORIZED, refreshResponse.getStatusCode());
    }

}
