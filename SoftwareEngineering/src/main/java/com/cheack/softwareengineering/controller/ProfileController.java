package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.RatingHistogramDto;
import com.cheack.softwareengineering.dto.ReviewCardDto;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.UserProfileSummaryDto;
import com.cheack.softwareengineering.dto.profile.BackgroundImageResponse;
import com.cheack.softwareengineering.dto.profile.ProfileImageResponse;
import com.cheack.softwareengineering.dto.profile.ProfileResponse;
import com.cheack.softwareengineering.dto.profile.UpdateGoalRequest;
import com.cheack.softwareengineering.dto.profile.UpdateProfileRequest;
import com.cheack.softwareengineering.service.ProfileService;
import com.cheack.softwareengineering.service.UserService;
import com.cheack.softwareengineering.service.StatsService;
import com.cheack.softwareengineering.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Profile API v1
 *
 * Base: /api/v1/profiles
 * Auth: Bearer 액세스 토큰(본인 수정 시 필수)
 */
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final StatsService statsService;
    private final ReviewService reviewService;


    private boolean include(String include, String key) {
        if (include == null || include.isBlank()) {
            return false;
        }
        // "stars,reviews" 처럼 들어오는 것을 가정
        String[] parts = include.split(",");
        for (String part : parts) {
            if (key.equalsIgnoreCase(part.trim())) {
                return true;
            }
        }
        return false;
    }


    /**
     * [프로필 조회(타인/본인 공용)]
     * GET /api/v1/profiles/{username}
     *
     * 쿼리: include=stars,reviews (현재는 무시, 확장 여지를 위해 파라미터만 받음)
     * 응답: 사용자 식별(username, nickname), intro, 프로필/배경 이미지 URL,
     *      팔로워/팔로잉 수, COMPLETED 책 수 등
     */
    @GetMapping("/{username}")
    public ProfileResponse getProfile(
            @PathVariable String username,
            @RequestParam(value = "include", required = false) String include,
            @AuthenticationPrincipal String viewerUsername
            // 로그인 안 했으면 null 가능
    ) {
        // username -> userId
        UserDto targetUser = userService.getByUsername(username);
        Long targetUserId = targetUser.getId();

        // 공개 프로필 요약 정보
        UserProfileSummaryDto summary =
                userService.getPublicProfileSummary(targetUser.getId());

        // monthlyGoal 은 아직 Profile 엔티티에 없으므로 null
        Integer monthlyGoal = null;

        RatingHistogramDto stars = null;
        Page<ReviewCardDto> reviews = null;

        if (include(include, "stars")) {
            stars = statsService.getRatingHistogram(targetUserId);
        }

        if (include(include, "reviews")) {
            // 프로필에서 보여줄 서평은 첫 페이지 N개 정도만 가져오는 식으로
            Pageable pageable = PageRequest.of(0, 10);
            reviews = reviewService.getPublicByUser(targetUserId, pageable);
        }

        // /{username} 에서는 email 정보는 채우지 않는다(공개용)
        return ProfileResponse.from(summary, null, monthlyGoal, stars, reviews);
    }

    /**
     * [내 프로필 조회]
     * GET /api/v1/profiles/me (Auth)
     *
     * 응답: 위와 동일 + email, emailVerified 등 본인 전용 필드 포함 가능
     */
    @GetMapping("/me")
    public ProfileResponse getMyProfile(
            @AuthenticationPrincipal String username,
            @RequestParam(value = "include", required = false) String include,
            @AuthenticationPrincipal String viewerUsername   // 로그인 안 했으면 null 가능
    ) {
        // 기본 유저 정보
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        // 공개 프로필 요약 + 팔로워/팔로잉/완독 수
        UserProfileSummaryDto summary = userService.getPublicProfileSummary(userId);

        // TODO: Profile 엔티티에 monthlyGoal 필드 추가 후 값 조회
        Integer monthlyGoal = null;

        RatingHistogramDto stars = null;
        Page<ReviewCardDto> reviews = null;

        if (include(include, "stars")) {
            stars = statsService.getRatingHistogram(userId);
        }

        if (include(include, "reviews")) {
            Pageable pageable = PageRequest.of(0, 10);
            reviews = reviewService.getPublicByUser(userId, pageable);
        }

        return ProfileResponse.from(summary, user, monthlyGoal, stars, reviews);
    }

    /**
     * [프로필 수정(닉네임/소개)]
     * PATCH /api/v1/profiles/me (Auth)
     *
     * 요청 필드: nickname, intro (둘 중 일부만 보내도 됨)
     * 응답: 204 No Content
     */
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateProfileRequest request
    ) {
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        // 닉네임 변경은 User 도메인 책임
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            userService.updateNickname(userId, request.getNickname());
        }

        // 소개 변경은 Profile 도메인 책임
        if (request.getIntro() != null) {
            profileService.updateIntro(userId, request.getIntro());
        }
    }

    /**
     * [프로필 이미지 업로드]
     * PUT /api/v1/profiles/me/image (Auth, multipart)
     *
     * 요청: file 파트(이미지)
     * 응답 필드: profileImageUrl
     */
    @PutMapping(
            value = "/me/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProfileImageResponse updateProfileImage(
            @AuthenticationPrincipal String username,
            @RequestPart("file") MultipartFile file
    ) {
        if (username == null) {
            // 토큰 없는 경우 방어
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }

        UserDto me = userService.getByUsername(username);
        Long userId = me.getId();

        String url = profileService.updateAvatar(userId, file);
        return new ProfileImageResponse(url);
    }

    /**
     * [배경 이미지 업로드]
     * PUT /api/v1/profiles/me/background (Auth, multipart)
     *
     * 요청: file 파트(이미지)
     * 응답 필드: backgroundImageUrl
     */
    @PutMapping(
            value = "/me/background",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BackgroundImageResponse updateBackgroundImage(
            @AuthenticationPrincipal String username,
            @RequestPart("file") MultipartFile file
    ) {
        UserDto me = userService.getByUsername(username);
        Long userId = me.getId();
        String url = profileService.updateBackground(userId, file);
        return new BackgroundImageResponse(url);
    }

    /**
     * [이달의 목표 설정]
     * PATCH /api/v1/profiles/me/goal (Auth)
     *
     * 요청 필드: monthlyGoal (0 이하면 해제 처리 가능)
     * 응답: 204 No Content
     */
    @PatchMapping("/me/goal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGoal(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateGoalRequest request
    ) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }

        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        profileService.setMonthlyGoal(userId, request.getMonthlyGoal());
    }
}
