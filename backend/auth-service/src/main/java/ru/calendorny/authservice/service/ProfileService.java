package ru.calendorny.authservice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.dto.response.UserProfile;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.exception.NotFoundException;
import ru.calendorny.authservice.metric.RegisterUserMetric;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final RegisterUserMetric registerUserMetric;

    public UserProfile getUserProfile(UUID id) {
        log.debug("Get user profile by id: {}", id);
        Profile profile = profileRepository.findByUserId(id)
            .orElseThrow(() -> new NotFoundException("Profile by id=%s not found".formatted(id.toString())));
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Account by id=%s not found".formatted(id.toString())));
        return new UserProfile(
            profile.getFirstName(),
            profile.getLastName(),
            profile.getBirthDate(),
            profile.getPhoneNumber(),
            account.getEmail()
        );
    }

    public void save(UUID id, UserProfileEdit userProfileEdit) {
        log.debug("Save user profile: {}", userProfileEdit);
        Profile profile = convertToProfile(userProfileEdit, id);
        profileRepository.save(profile);
        registerUserMetric.incrementRegisteredUsers();

    }

    private Profile convertToProfile(UserProfileEdit userProfileEdit, UUID id) {
        return Profile.builder()
            .userId(id)
            .firstName(userProfileEdit.firstName())
            .lastName(userProfileEdit.lastName())
            .birthDate(userProfileEdit.birthdate())
            .phoneNumber(userProfileEdit.phoneNumber())
            .timezone(userProfileEdit.timezone())
            .language(userProfileEdit.language())
            .telegram(userProfileEdit.telegram())
            .build();
    }

    public UserProfileEdit getUserProfileEdit(UUID id) {
        log.debug("Get user profile edit by id: {}", id);
        Profile profile = profileRepository.findByUserId(id)
            .orElseThrow(() -> new NotFoundException("Profile by id=%s not found".formatted(id.toString())));
        return new UserProfileEdit(
            profile.getFirstName(),
            profile.getFirstName(),
            profile.getBirthDate(),
            profile.getPhoneNumber(),
            profile.getTimezone(),
            profile.getLanguage(),
            profile.getTelegram()
        );
    }

    public void updateProfile(UUID id, UserProfileEdit userProfileEdit) {
        log.debug("Update user profile by id: {}", id);
        profileRepository.merge(id, userProfileEdit);
    }
}
