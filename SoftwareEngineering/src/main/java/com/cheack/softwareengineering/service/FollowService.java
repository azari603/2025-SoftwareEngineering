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
            return;
        }

        follow.setStatus(true);
        follow.setUpdatedAt(LocalDateTime.now());
        followRepository.save(follow);

        notificationService.create(
                followeeId,
                followerId,
                NotificationType.FOLLOW,
                "/profiles/" + follower.getUsername(),
                null,
                null
        );
    }

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

    public boolean isFollowing(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .map(Follow::getStatus)
                .orElse(false);
    }

    public long countFollowers(Long userId) {
        return followRepository.countByFolloweeIdAndStatusTrue(userId);
    }

    public long countFollowings(Long userId) {
        return followRepository.countByFollowerIdAndStatusTrue(userId);
    }

    public Page<UserMiniDto> getFollowers(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFolloweeIdAndStatusTrue(userId, pageable);
        List<Long> followerIds = page.getContent().stream().map(Follow::getFollowerId).toList();
        Map<Long, User> userMap = userRepository.findAllById(followerIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<UserMiniDto> dtos = page.getContent().stream()
                .map(f -> userMap.get(f.getFollowerId()))
                .filter(Objects::nonNull)
                .map(this::toMiniDto)
                .toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public Page<UserMiniDto> getFollowings(Long userId, Pageable pageable) {
        Page<Follow> page = followRepository.findByFollowerIdAndStatusTrue(userId, pageable);
        List<Long> followeeIds = page.getContent().stream().map(Follow::getFolloweeId).toList();
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