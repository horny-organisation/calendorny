package ru.calendorny.taskservice.service;

import java.util.UUID;

public interface JwtService {

    UUID getUserIdFromAccessToken(String accessToken);
}
