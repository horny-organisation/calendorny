package ru.calendorny.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email) {}
