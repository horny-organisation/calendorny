package ru.calendorny.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service")
public class AuthServiceClient {
}
