package ru.calendorny.securitystarter;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email) {}
