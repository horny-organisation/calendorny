package ru.calendorny.authservice.integration;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.calendorny.authservice.TestcontainersConfiguration;
import ru.calendorny.authservice.dto.request.LoginRequest;
import ru.calendorny.authservice.dto.request.RegisterRequest;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.dto.response.ApiErrorResponse;
import ru.calendorny.authservice.dto.response.AuthTokens;
import ru.calendorny.authservice.dto.response.UserProfile;
import ru.calendorny.authservice.dto.response.ValidationErrorResponse;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.ProfileRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ProfileControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private String accessToken;

    /*
     * Registering user for profile checks
     * */
    @BeforeAll
    void cleanDatabaseAndRegisterUserAndLoginAndSaveAccessToken() {
        jdbcTemplate.execute("TRUNCATE TABLE refresh_tokens, profiles, accounts RESTART IDENTITY CASCADE");


        RegisterRequest req = new RegisterRequest("ex@ya.ru", "12345678", "Alex", "Albon");
        ResponseEntity<String> regResponse = restTemplate.postForEntity("/api/v1/register",
            req, String.class);

        if (regResponse.getStatusCode() != HttpStatus.CREATED) {
            throw new IllegalStateException("registration failed - unexpected response status: " + regResponse.getStatusCode());
        }

        LoginRequest loginRequest = new LoginRequest("ex@ya.ru", "12345678");
        ResponseEntity<AuthTokens> loginResponse = restTemplate.postForEntity("/api/v1/login", loginRequest, AuthTokens.class);
        if (loginResponse.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("login failed - unexpected response status: " + loginResponse.getStatusCode());
        }
        accessToken = loginResponse.getBody().accessToken();
    }

    @Test
    @Order(1)
    void newUserProfileIsEmpty() {
        ResponseEntity<UserProfile> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.GET, withAuthHeaders(), UserProfile.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alex", response.getBody().firstName());
        assertEquals("Albon", response.getBody().lastName());
        assertNull(response.getBody().birthDate());
        assertNull(response.getBody().phoneNumber());
    }

    @Test
    @Order(1)
    void nonAuthenticatedRequestForProfileShouldFail() {
        ResponseEntity<ApiErrorResponse> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.GET, null, ApiErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().code());
    }

    @Test
    @Order(2)
    void newUserProfileEditIsEmpty() {
        ResponseEntity<UserProfileEdit> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.GET, withAuthHeaders(), UserProfileEdit.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alex", response.getBody().firstName());
        assertEquals("Albon", response.getBody().lastName());
        assertNull(response.getBody().birthdate());
        assertNull(response.getBody().phoneNumber());
        assertNull(response.getBody().timezone());
        assertNull(response.getBody().language());
        assertNull(response.getBody().telegram());
    }

    @Test
    @Order(2)
    void nonAuthenticatedRequestForUserProfileEditShouldFail() {
        ResponseEntity<ApiErrorResponse> response =
            restTemplate.exchange("/api/v1/profile/edit", HttpMethod.GET, null, ApiErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().code());
    }

    @Test
    @Order(3)
    void patchUserProfileShouldSuccess() {
        UserProfileEdit editRequest = new UserProfileEdit(
            "Michael",
            "Schumacher",
            LocalDate.of(1996, 3, 23),
            "+79991231111",
            "Europe/Moscow",
            "ru",
            "@formula1"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserProfileEdit> entity = new HttpEntity<>(editRequest, headers);


        ResponseEntity<Void> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.PATCH, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        UUID userId = accountRepository.findByEmail("ex@ya.ru").get().getId();
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();

        assertEquals("Michael", profile.getFirstName());
        assertEquals("Schumacher", profile.getLastName());
        assertEquals(LocalDate.of(1996, 3, 23), profile.getBirthDate());
        assertEquals("+79991231111", profile.getPhoneNumber());
        assertEquals("@formula1", profile.getTelegram());
        assertEquals("Europe/Moscow", profile.getTimezone());
        assertEquals("ru", profile.getLanguage());
    }

    @Test
    @Order(4)
    void partialPatchUserProfileShouldSuccess() {
        UserProfileEdit editRequest = new UserProfileEdit(
            null,
            null,
            LocalDate.of(2005, 4, 23),
            "+1234567890",
            null,
            "en",
            "@formula2"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserProfileEdit> entity = new HttpEntity<>(editRequest, headers);


        ResponseEntity<Void> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.PATCH, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        UUID userId = accountRepository.findByEmail("ex@ya.ru").get().getId();
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();

        assertEquals("Michael", profile.getFirstName());
        assertEquals("Schumacher", profile.getLastName());
        assertEquals(LocalDate.of(2005, 4, 23), profile.getBirthDate());
        assertEquals("+1234567890", profile.getPhoneNumber());
        assertEquals("@formula2", profile.getTelegram());
        assertEquals("Europe/Moscow", profile.getTimezone());
        assertEquals("en", profile.getLanguage());
    }

    @Test
    @Order(5)
    void partialPatchUserProfileWithConstraintErrorsShouldFail() {
        UserProfileEdit editRequest = new UserProfileEdit(
            " ",
            " ",
            LocalDate.of(1996, 3, 23),
            " ",
            "Europe/Moscow",
            "ru",
            "@formula1"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserProfileEdit> entity = new HttpEntity<>(editRequest, headers);


        ResponseEntity<ValidationErrorResponse> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.PATCH, entity, ValidationErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        UUID userId = accountRepository.findByEmail("ex@ya.ru").get().getId();
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();

        assertEquals("Michael", profile.getFirstName());
        assertEquals("Schumacher", profile.getLastName());
        assertEquals(LocalDate.of(2005, 4, 23), profile.getBirthDate());
        assertEquals("+1234567890", profile.getPhoneNumber());
        assertEquals("@formula2", profile.getTelegram());
        assertEquals("Europe/Moscow", profile.getTimezone());
        assertEquals("en", profile.getLanguage());

        assertEquals(400, response.getBody().code());
        List<ValidationErrorResponse.ValidationError> expectedErrors = List.of(
            new ValidationErrorResponse.ValidationError("lastName", "lastName length must be between 2 and 100"),
            new ValidationErrorResponse.ValidationError("phoneNumber", "phoneNumber length must be between 9 and 20"),
            new ValidationErrorResponse.ValidationError("firstName", "firstName length must be between 2 and 100")
        );
        System.out.println(response.getBody().validationErrors());
        assertTrue(expectedErrors.containsAll(response.getBody().validationErrors()));
        System.out.println(response.getBody());
    }

    @Test
    void nonAuthenticatedRequestForPatchUserProfileShouldFail() {
        UserProfileEdit editRequest = new UserProfileEdit(
            "Michael",
            "Schumacher",
            LocalDate.of(1996, 3, 23),
            "+79991231111",
            "Europe/Moscow",
            "ru",
            "@formula1"
        );
        HttpEntity<UserProfileEdit> entity = new HttpEntity<>(editRequest);


        ResponseEntity<Void> response =
            restTemplate.exchange("/api/v1/profile", HttpMethod.PATCH, entity, Void.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private HttpEntity<Void> withAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(headers);
    }

}
