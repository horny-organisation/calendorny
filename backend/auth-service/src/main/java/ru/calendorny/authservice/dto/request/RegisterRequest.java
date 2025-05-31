package ru.calendorny.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email(message = "incorrect email")
    @NotBlank(message = "email must not be blank")
    @Size(max = 254, message = "email should be less then {max} chars")
    String email,

    @Size(min = 8, message = "min pass length is {min}")
    String password,

    @Size(min = 2, max = 100, message = "min firstName length is {min}, max is {max}")
    String firstName,

    @Size(min = 2, max = 100, message = "min lastName length is {min}, max is {max}")
    String lastName
) {}
