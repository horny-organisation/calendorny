package ru.calendorny.authservice.dto.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UserProfileEdit(
    String firstName,
    String lastName,
    LocalDate birthdate,
    String phoneNumber,
    String timezone,
    String language,
    String telegram
) {
}
