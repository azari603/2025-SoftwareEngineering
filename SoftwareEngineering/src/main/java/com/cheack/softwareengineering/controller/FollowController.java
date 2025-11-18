package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.FollowCountResponse;
import com.cheack.softwareengineering.dto.FollowStatusResponse;
import com.cheack.softwareengineering.dto.FollowerDto;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.UserMiniDto;
import com.cheack.softwareengineering.service.FollowService;
import com.cheack.softwareengineering.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Follow API v1
 *
 * Base: /api/v1/follows
 *
 * [팔로우 생성]
 *  POST   /api/v1/follows/{targetUsername}
 * [팔로우 취소]
 *  DELETE /api/v1/follows/{targetUsername}
 * [팔로우 상태 조회]
 *  GET    /api/v1/follows/{targetUsername}/status
 * [팔로워 목록]
 *  GET    /api/v1/follows/{username}/followers
 * [팔로잉 목록]
 *  GET    /api/v1/follows/{username}/following
 * [팔로우 카운트]
 *  GET    /api/v1/follows/{username}/counts
 */
@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    /**
     * [팔로우 생성]
     * POST /api/v1/follows/{targetUsername}
     * Auth 필요
     */
    @PostMapping("/{targetUsername}")
    public ResponseEntity<Void> follow(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal(expression = "id") Long followerId
    ) {
        if (followerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDto target = userService.getByUsername(targetUsername);
        followService.follow(followerId, target.getId());

        // 멱등이지만, "팔로우" 액션이므로 201 선택
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * [팔로우 취소]
     * DELETE /api/v1/follows/{targetUsername}
     * Auth 필요
     */
    @DeleteMapping("/{targetUsername}")
    public ResponseEntity<Void> unfollow(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal(expression = "id") Long followerId
    ) {
        if (followerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDto target = userService.getByUsername(targetUsername);
        followService.unfollow(followerId, target.getId());

        return ResponseEntity.noContent().build();
    }

    /**
     * [팔로우 상태 조회]
     * GET /api/v1/follows/{targetUsername}/status
     *
     * 로그인 안 되어 있으면 following=false
     */
    @GetMapping("/{targetUsername}/status")
    public ResponseEntity<FollowStatusResponse> status(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal(expression = "id") Long viewerId
    ) {
        if (viewerId == null) {
            return ResponseEntity.ok(new FollowStatusResponse(false));
        }

        UserDto target = userService.getByUsername(targetUsername);
        boolean following = followService.isFollowing(viewerId, target.getId());

        return ResponseEntity.ok(new FollowStatusResponse(following));
    }

    /**
     * [팔로워 목록]
     * GET /api/v1/follows/{username}/followers?page,size,sort
     *
     * 응답 항목: username, nickname, profileImageUrl, followedByMe(true|false)
     */
    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<FollowerDto>> followers(
            @PathVariable String username,
            @AuthenticationPrincipal(expression = "id") Long viewerId,
            Pageable pageable
    ) {
        UserDto target = userService.getByUsername(username);

        Page<UserMiniDto> followerPage =
                followService.getFollowers(target.getId(), pageable);

        Page<FollowerDto> dtoPage = followerPage.map(u -> {
            boolean followedByMe = false;
            if (viewerId != null) {
                // viewer → follower 를 다시 팔로우하는지 여부
                followedByMe = followService.isFollowing(viewerId, u.getId());
            }

            return FollowerDto.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .nickname(u.getNickname())
                    .profileImageUrl(null) // 지금은 프로필 서비스 안 붙였으니 null 고정
                    .followedByMe(followedByMe)
                    .build();
        });

        return ResponseEntity.ok(dtoPage);
    }

    /**
     * [팔로잉 목록]
     * GET /api/v1/follows/{username}/following?page,size,sort
     *
     * 응답 항목: username, nickname, profileImageUrl
     */
    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserMiniDto>> following(
            @PathVariable String username,
            Pageable pageable
    ) {
        UserDto target = userService.getByUsername(username);
        Page<UserMiniDto> page =
                followService.getFollowings(target.getId(), pageable);

        return ResponseEntity.ok(page);
    }

    /**
     * [팔로우 카운트]
     * GET /api/v1/follows/{username}/counts
     *
     * 응답: followerCount, followingCount
     */
    @GetMapping("/{username}/counts")
    public ResponseEntity<FollowCountResponse> counts(
            @PathVariable String username
    ) {
        UserDto target = userService.getByUsername(username);
        long followerCount = followService.countFollowers(target.getId());
        long followingCount = followService.countFollowings(target.getId());

        return ResponseEntity.ok(new FollowCountResponse(followerCount, followingCount));
    }
}