package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.UserMiniDto;
import com.cheack.softwareengineering.entity.Follow;
import com.cheack.softwareengineering.entity.NotificationType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.FollowRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 팔로우 생성
     * - 자기 자신 팔로우 불가
     * - 이미 팔로우 중이면 아무 일도 안 함(멱등)
     * - 과거에 끊었다가 다시 팔로우하면 status=true 로 복구
     */
    @Transactional
    public void follow(Long followerId, Long followeeId) {
        if (Objects.equals(followerId, followeeId)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 하는 사용자를 찾을 수 없습니다."));
        userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 사용자를 찾을 수 없습니다."));

        Follow follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseGet(() -> Follow.builder()
                        .followerId(followerId)
                        .followeeId(followeeId)
                        .createdAt(LocalDateTime.now())
                        .build());

        if (Boolean.TRUE.equals(follow.getStatus())) {
            // 이미 팔로우 중이면 그대로 종료(멱등)
            return;
        }

        follow.setStatus(true);
        follow.setUpdatedAt(LocalDateTime.now());
        followRepository.save(follow);

        // 팔로우 알림 (reviewId는 없으므로 null 전달)
        String targetUrl = "/profiles/" + follower.getUsername();
        String content = follower.getNickname() + "님이 회원님을 팔로우하기 시작했습니다.";

        notificationService.create(
                followeeId,                 // receiverId
                followerId,                 // actorId
                NotificationType.FOLLOW,    // type
                targetUrl,
                content,
                null                        // reviewId 없음
        );
    }

    /**
     * 언팔로우
     * - 기록은 남기고 status=false 로만 바꿈 (나중에 복구 가능)
     * - 이미 언팔 상태여도 에러 없이 멱등 처리
     */
    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(f -> {
                    if (Boolean.TRUE.equals(f.getStatus())) {
                        f.setStatus(false);
                        f.setUpdatedAt(LocalDateTime.now());
                        followRepository.save(f);
                    }
                });
    }

    /**
     * 팔로우 여부 조회
     */
    public boolean isFollowing(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .map(Follow::getStatus)
                .orElse(false);
    }

    /**
     * 나를 팔로우하는 사람 수
     */
    public long countFollowers(Long userId) {
        return followRepository.countByFolloweeIdAndStatusTrue(userId);
    }

    /**
     * 내가 팔로우하는 사람 수
     */
    public long countFollowings(Long userId) {
        return followRepository.countByFollowerIdAndStatusTrue(userId);
    }

    /**
     * 팔로워 목록
     */
    public Page<UserMiniDto> getFollowers(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFolloweeIdAndStatusTrue(userId, pageable);

        List<Long> followerIds = page.getContent().stream()
                .map(Follow::getFollowerId)
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UserMiniDto> dtos = page.getContent().stream()
                .map(f -> userMap.get(f.getFollowerId()))
                .filter(Objects::nonNull)
                .map(this::toMiniDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 팔로잉 목록
     */
    public Page<UserMiniDto> getFollowings(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFollowerIdAndStatusTrue(userId, pageable);

        List<Long> followeeIds = page.getContent().stream()
                .map(Follow::getFolloweeId)
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(followeeIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UserMiniDto> dtos = page.getContent().stream()
                .map(f -> userMap.get(f.getFolloweeId()))
                .filter(Objects::nonNull)
                .map(this::toMiniDto)
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private UserMiniDto toMiniDto(User user) {
        return UserMiniDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}