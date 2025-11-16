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

    /**
     * 회원가입 직후 기본 프로필 생성
     * - profiles 테이블에 user_id 기준 unique 로 한 건 생성
     */
    @Transactional
    public void createDefaultProfile(Long userId) {
        // User 존재 여부 확인
        User user = findUserOrThrow(userId);

        // 이미 프로필 있으면 아무 것도 안 함 (idempotent)
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }

        Profile profile = Profile.builder()
                .userId(user.getId())
                .userImage(null)        // 기본 이미지 없음
                .backgroundImage(null)  // 기본 배경 없음
                .readBook(0L)           // 기본 값 0
                .intro("")              // 기본 소개 빈 문자열
                .build();

        profileRepository.save(profile);
    }

    /**
     * 프로필 조회 (내 프로필 / 다른 사람 프로필 공용)
     * - viewerId 는 나중에 "isMe", "isFollowing" 같은 플래그 계산에 쓸 여지용
     */
    public ProfileDto getProfile(Long viewerId, Long targetUserId) {
        Profile profile = findProfileByUserIdOrThrow(targetUserId);
        // viewerId 를 사용한 추가 로직이 생기면 여기에서 처리
        return ProfileDto.from(profile);
    }

    /**
     * 한 줄 소개 수정
     */
    @Transactional
    public void updateIntro(Long userId, String intro) {
        Profile profile = findProfileByUserIdOrThrow(userId);

        String trimmed = (intro == null) ? "" : intro.trim();
        profile.setIntro(trimmed);

        profileRepository.save(profile);
    }

    /**
     * 아바타(프로필 이미지) 수정
     * @return 새로 설정된 아바타의 public URL
     */
    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        Profile profile = findProfileByUserIdOrThrow(userId);

        // 이미지 검증 (MIME, 크기, 해상도 등)
        imageValidator.validateAll(file);

        // 기존 이미지가 있으면 삭제
        String oldKey = profile.getUserImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
        }

        // 새 이미지 저장 (objectKey 저장)
        String objectKey = imageStorage.storeAvatar(userId, file);
        profile.setUserImage(objectKey);
        profileRepository.save(profile);

        // 클라이언트에 반환할 public URL
        return imageStorage.toPublicUrl(objectKey);
    }

    /**
     * 배경 이미지 수정
     * @return 새로 설정된 배경 이미지의 public URL
     */
    @Transactional
    public String updateBackground(Long userId, MultipartFile file) {
        Profile profile = findProfileByUserIdOrThrow(userId);

        // 이미지 검증
        imageValidator.validateAll(file);

        String oldKey = profile.getBackgroundImage();
        if (oldKey != null) {
            imageStorage.delete(oldKey);
        }

        String objectKey = imageStorage.storeBackground(userId, file);
        profile.setBackgroundImage(objectKey);
        profileRepository.save(profile);

        return imageStorage.toPublicUrl(objectKey);
    }

    /**
     * 아바타 제거
     */
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

    /**
     * 배경 이미지 제거
     */
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

    /**
     * 월별 목표 설정
     * - 현재 Profile 엔티티에는 monthlyGoal 필드가 없으므로
     *   엔티티에 필드 추가 후 setMonthlyGoal 호출 부분을 구현해야 한다.
     */
    @Transactional
    public void setMonthlyGoal(Long userId, int goal) {
        Profile profile = findProfileByUserIdOrThrow(userId);

        // TODO: Profile 엔티티에 monthlyGoal 필드 추가 후 아래 라인 구현
        // profile.setMonthlyGoal(goal);

        profileRepository.save(profile);
    }

    // ==================== 내부 헬퍼 ====================

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private Profile findProfileByUserIdOrThrow(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for user: " + userId));
    }
}
