package ru.calendorny.authservice.entity;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Profile {

    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String telegram;
    private String timezone;
    private String language;

    public Profile(UUID userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Profile(
            UUID userId,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phoneNumber,
            String telegram,
            String timezone,
            String language) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.telegram = telegram;
        this.timezone = timezone;
        this.language = language;
    }
}
