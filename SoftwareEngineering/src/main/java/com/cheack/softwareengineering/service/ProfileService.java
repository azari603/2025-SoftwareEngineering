package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.Profile;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.ProfileRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import com.cheack.softwareengineering.storage.ImageStorage;
import com.cheack.softwareengineering.storage.ImageValidator;
import com.cheack.softwareengineering.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ImageStorage imageStorage;
    private final ImageValidator imageValidator;

    @Transactional
    public void createDefaultProfile(Long userId) {
        User user = findUserOrThrow(userId);
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }
        Profile profile = Profile.builder()
                .userId(user.getId())
                .userImage(null)
                .backgroundImage(null)
                .readBook(0L)
                .intro("")
                .monthlyGoal(null)
                .build();
        profileRepository.save(profile);
    }

    public ProfileDto getProfile(Long viewerId, Long targetUserId) {
        Profile profile = findProfileByUserIdOrThrow(targetUserId);
        return ProfileDto.from(profile);
    }

    @Transactional
    public void updateIntro(Long userId, String intro) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = findUserOrThrow(userId);
                    Profile p = Profile.builder()
                            .userId(user.getId())
                            .userImage(null)
                            .backgroundImage(null)
                            .readBook(0L)
                            .intro("")
                            .monthlyGoal(null)
                            .build();
                    return profileRepository.save(p);
                });

        String trimmed = (intro == null) ? "" : intro.trim();
        profile.setIntro(trimmed);
        profileRepository.save(profile);
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 필요합니다.");
        }
        imageValidator.validateAll(file);

        Profile profile = getOrCreateProfile(userId);

        String oldKey = profile.getUserImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
        }

        String objectKey = imageStorage.storeAvatar(userId, file);
        profile.setUserImage(objectKey);
        profileRepository.save(profile);

        return imageStorage.toPublicUrl(objectKey);
    }

    @Transactional
    public String updateBackground(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 필요합니다.");
        }
        imageValidator.validateAll(file);

        Profile profile = getOrCreateProfile(userId);

        String oldKey = profile.getBackgroundImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
        }

        String objectKey = imageStorage.storeBackground(userId, file);
        profile.setBackgroundImage(objectKey);
        profileRepository.save(profile);

        return imageStorage.toPublicUrl(objectKey);
    }

    @Transactional
    public void removeAvatar(Long userId) {
        Profile profile = findProfileByUserIdOrThrow(userId);
        String oldKey = profile.getUserImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
            profile.setUserImage(null);
            profileRepository.save(profile);
        }
    }

    @Transactional
    public void removeBackground(Long userId) {
        Profile profile = findProfileByUserIdOrThrow(userId);
        String oldKey = profile.getBackgroundImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
            profile.setBackgroundImage(null);
            profileRepository.save(profile);
        }
    }

    @Transactional
    public void setMonthlyGoal(Long userId, int goal) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = findUserOrThrow(userId);
                    Profile p = Profile.builder()
                            .userId(user.getId())
                            .userImage(null)
                            .backgroundImage(null)
                            .readBook(0L)
                            .intro("")
                            .monthlyGoal(null)
                            .build();
                    return profileRepository.save(p);
                });

        if (goal <= 0) {
            profile.setMonthlyGoal(null);
        } else {
            profile.setMonthlyGoal(goal);
        }
        profileRepository.save(profile);
    }

    private Profile getOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            User user = findUserOrThrow(userId);
            Profile p = Profile.builder()
                    .userId(user.getId())
                    .userImage(null)
                    .backgroundImage(null)
                    .readBook(0L)
                    .intro("")
                    .monthlyGoal(null)
                    .build();
            return profileRepository.save(p);
        });
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private Profile findProfileByUserIdOrThrow(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for user: " + userId));
    }
}