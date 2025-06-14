package ru.calendorny.eventservice.security;

import java.util.UUID;

public record AuthenticatedUser(UUID id, String email) {}
