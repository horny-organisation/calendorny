package ru.calendorny.taskservice.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email) {}
