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
            return null;
        } catch (Exception e) {
            throw new UnauthorizedAccessException();
        }
    }
}
