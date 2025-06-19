package ru.calendorny.authservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserProfileEdit(
    @Size(min = 2, max = 100, message = "firstName length must be between {min} and {max}")
    String firstName,

    @Size(min = 2, max = 100, message = "lastName length must be between {min} and {max}")
    String lastName,

    @NotNull(message = "birth date can not be null")
    @Past(message = "birthdate must be in the past")
    LocalDate birthdate,

    @Size(min = 9, max = 20, message = "phoneNumber length must be between {min} and {max}")
    String phoneNumber,

    @Size(min = 2, max = 50, message = "timezone length must be between {min} and {max}")
    String timezone,

    @Size(min = 2, max = 10, message = "language length must be between {min} and {max}")
    String language,

    @Size(min = 5, max = 100, message = "telegram length must be between {min} and {max}")
    String telegram
) {
}
