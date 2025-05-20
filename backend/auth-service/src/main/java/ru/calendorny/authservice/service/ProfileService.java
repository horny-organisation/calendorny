package ru.calendorny.authservice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.dto.response.UserProfile;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.exception.NotFoundException;
import ru.calendorny.authservice.repository.AccountRepository;
import ru.calendorny.authservice.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;

    public UserProfile getUserProfile(UUID id) {
        Profile profile = profileRepository.findByUserId(id)
            .orElseThrow(() -> new NotFoundException("Profile by id=%s not found".formatted(id.toString())));
        return new UserProfile(
            profile.getFirstName(),
            profile.getLastName(),
            profile.getBirthDate(),
            profile.getPhoneNumber(),
            profile.getPhoneNumber()
        );
    }

//    public void update()

    public void save(UUID id, UserProfileEdit userProfileEdit) {
        Profile profile = convertToProfile(userProfileEdit, id);
        profileRepository.save(profile);
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

}
