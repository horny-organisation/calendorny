package ru.calendorny.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email(message = "incorrect email") @NotBlank(message = "incorrect email") String email,
    @Size(min = 8, message = "min pass length is 8") String password,
    @Size(min = 2, max = 100, message = "min firstName length is 2, max is 100") String firstName,
    @Size(min = 2, max = 100, message = "min lastName length is 2, max is 100") String lastName) {
}
