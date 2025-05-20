package ru.calendorny.authservice.dto.response;

import java.time.LocalDate;

public record UserProfile(
    String firstName,
    String lastName,
    LocalDate birthDate,
    String phoneNumber,
    String email
) {
}
