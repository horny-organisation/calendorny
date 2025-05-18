package ru.calendorny.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min = 8) String password,
        @Size(min = 2, max = 100) String firstName,
        @Size(min = 2, max = 100) String lastName) {}
