package ru.calendorny.authservice.controller;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.dto.response.UserProfile;
import ru.calendorny.authservice.security.AuthenticatedUser;
import ru.calendorny.authservice.service.ProfileService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getInfo() {
        return Map.of("message", "public information");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserProfile profile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return profileService.getUserProfile(authenticatedUser.id());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/edit")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileEdit profileEdit(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return profileService.getUserProfileEdit(authenticatedUser.id());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void profile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @Valid @RequestBody UserProfileEdit userProfileEdit) {
        profileService.updateProfile(authenticatedUser.id(), userProfileEdit);
    }

}
