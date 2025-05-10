package ru.calendorny.taskservice.service.impl;

import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.exception.UnauthorizedAccessException;
import ru.calendorny.taskservice.service.JwtService;
import java.util.UUID;

@Service
public class BaseJwtServiceImpl implements JwtService {

    @Override
    public UUID getUserIdFromAccessToken(String accessToken) {
        try {
            //TODO: get user ID from access token logic
            return UUID.fromString("959416fb-8eb1-40ae-b769-3ea20a8a333e");
        } catch (Exception e) {
            throw new UnauthorizedAccessException();
        }
    }
}
