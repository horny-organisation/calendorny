package ru.calendorny.authservice.entity;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Profile {

    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
    private String telegram;
    private String timezone;
    private String language;

}
