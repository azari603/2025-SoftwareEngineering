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
import com.cheack.softwareengineering.service.ReviewService;
import com.cheack.softwareengineering.service.StatsService;
import com.cheack.softwareengineering.service.UserService;
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
        String[] parts = include.split(",");
        for (String part : parts) {
            if (key.equalsIgnoreCase(part.trim())) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/{username}")
    public ProfileResponse getProfile(
            @PathVariable String username,
            @RequestParam(value = "include", required = false) String include
    ) {
        UserDto targetUser = userService.getByUsername(username);
        Long targetUserId = targetUser.getId();

        UserProfileSummaryDto summary = userService.getPublicProfileSummary(targetUserId);

        Integer monthlyGoal = null;
        RatingHistogramDto stars = null;
        Page<ReviewCardDto> reviews = null;

        if (include(include, "stars")) {
            stars = statsService.getRatingHistogram(targetUserId);
        }

        if (include(include, "reviews")) {
            Pageable pageable = PageRequest.of(0, 10);
            reviews = reviewService.getPublicByUserForProfile(
                    targetUserId,
                    targetUser.getUsername(),
                    summary.getNickname(),
                    summary.getProfileImageUrl(),
                    pageable
            );
        }

        return ProfileResponse.from(summary, null, monthlyGoal, stars, reviews);
    }

    @GetMapping("/me")
    public ProfileResponse getMyProfile(
            @AuthenticationPrincipal String username,
            @RequestParam(value = "include", required = false) String include
    ) {
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        UserProfileSummaryDto summary = userService.getPublicProfileSummary(userId);

        Integer monthlyGoal = null;
        RatingHistogramDto stars = null;
        Page<ReviewCardDto> reviews = null;

        if (include(include, "stars")) {
            stars = statsService.getRatingHistogram(userId);
        }

        if (include(include, "reviews")) {
            Pageable pageable = PageRequest.of(0, 10);
            reviews = reviewService.getPublicByUserForProfile(
                    userId,
                    user.getUsername(),
                    summary.getNickname(),
                    summary.getProfileImageUrl(),
                    pageable
            );
        }

        return ProfileResponse.from(summary, user, monthlyGoal, stars, reviews);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateProfileRequest request
    ) {
        UserDto user = userService.getByUsername(username);
        Long userId = user.getId();

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            userService.updateNickname(userId, request.getNickname());
        }
        if (request.getIntro() != null) {
            profileService.updateIntro(userId, request.getIntro());
        }
    }

    @PutMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileImageResponse updateProfileImage(
            @AuthenticationPrincipal String username,
            @RequestPart("file") MultipartFile file
    ) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일이 필요합니다.");
        }
        UserDto me = userService.getByUsername(username);
        Long userId = me.getId();
        String url = profileService.updateAvatar(userId, file);
        return new ProfileImageResponse(url);
    }

    @DeleteMapping("/me/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetProfileImage(@AuthenticationPrincipal String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
        UserDto me = userService.getByUsername(username);
        profileService.removeAvatar(me.getId());
    }

    @PutMapping(value = "/me/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BackgroundImageResponse updateBackgroundImage(
            @AuthenticationPrincipal String username,
            @RequestPart("file") MultipartFile file
    ) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일이 필요합니다.");
        }
        UserDto me = userService.getByUsername(username);
        Long userId = me.getId();
        String url = profileService.updateBackground(userId, file);
        return new BackgroundImageResponse(url);
    }

    @DeleteMapping("/me/background")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetBackgroundImage(@AuthenticationPrincipal String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다.");
        }
        UserDto me = userService.getByUsername(username);
        profileService.removeBackground(me.getId());
    }

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