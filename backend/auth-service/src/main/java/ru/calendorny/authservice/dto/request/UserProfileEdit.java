package ru.calendorny.authservice.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserProfileEdit(
    @Size(min = 2, max = 100, message = "min firstName length is 2, max is 100") String firstName,
    @Size(min = 2, max = 100, message = "min lastName length is 2, max is 100") String lastName,
    LocalDate birthdate,
    @Size(min = 9, max = 16, message = "min phone number length is 9, max is 16") String phoneNumber,
    @Size(min = 2, max = 64, message = "min timezone length is 2, max is 64") String timezone,
    @Size(min = 2, max = 16, message = "min language length is 2, max is 16") String language,
    @Size(min = 5, max = 128, message = "min language length is 5, max is 128") String telegram
) {
}
