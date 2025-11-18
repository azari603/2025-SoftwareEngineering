package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.UserMiniDto;
import com.cheack.softwareengineering.dto.UserProfileSummaryDto;
import com.cheack.softwareengineering.entity.Follow;
import com.cheack.softwareengineering.entity.Profile;
import com.cheack.softwareengineering.entity.ReadingStatusType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.FollowRepository;
import com.cheack.softwareengineering.repository.ProfileRepository;
import com.cheack.softwareengineering.repository.ReadingStatusRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;
    private final ReadingStatusRepository readingStatusRepository;

    /**
     * userId 기준 단건 조회
     */
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserDto.from(user);
    }

    /**
     * username 기준 단건 조회
     */
    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserDto.from(user);
    }

    /**
     * 공개 프로필 요약 정보
     * - 기본 유저 정보
     * - 프로필(이미지/소개)
     * - 팔로워/팔로잉 수
     * - COMPLETED 상태 도서 수
     */
    public UserProfileSummaryDto getPublicProfileSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Profile profile = profileRepository.findByUserId(userId)
                .orElse(null);

        long followerCount = followRepository.countByFolloweeIdAndStatusTrue(userId);
        long followingCount = followRepository.countByFollowerIdAndStatusTrue(userId);
        long completedBookCount =
                readingStatusRepository.countByUserIdAndStatus(userId, ReadingStatusType.COMPLETED);

        return UserProfileSummaryDto.from(
                user,
                profile,
                followerCount,
                followingCount,
                completedBookCount
        );
    }

    /**
     * 나를 팔로우하는 사람들 목록
     */
    public Page<UserMiniDto> getFollowers(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFolloweeIdAndStatusTrue(userId, pageable);

        List<Long> followerIds = page.getContent().stream()
                .map(Follow::getFollowerId)
                .toList();

        if (followerIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }

        Map<Long, User> userMap = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UserMiniDto> dtos = followerIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(UserMiniDto::from)   // 이미 Comment/FollowService에서 쓰던 정적 메서드
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 내가 팔로우하는 사람들 목록
     */
    public Page<UserMiniDto> getFollowings(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFollowerIdAndStatusTrue(userId, pageable);

        List<Long> followeeIds = page.getContent().stream()
                .map(Follow::getFolloweeId)
                .toList();

        if (followeeIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, page.getTotalElements());
        }

        Map<Long, User> userMap = userRepository.findAllById(followeeIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UserMiniDto> dtos = followeeIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(UserMiniDto::from)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 사용자 검색 (username / nickname 부분 일치)
     */
    public Page<UserMiniDto> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        Page<User> page = userRepository
                .findByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
                        keyword, keyword, pageable
                );

        return page.map(UserMiniDto::from);
    }
}