package com.cheack.softwareengineering.dto.profile;

import com.cheack.softwareengineering.dto.RatingHistogramDto;
import com.cheack.softwareengineering.dto.UserDto;
import com.cheack.softwareengineering.dto.UserProfileSummaryDto;
import com.cheack.softwareengineering.dto.ReviewCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 조회 응답 DTO
 * - /profiles/{username}, /profiles/me 공용
 * - /me 에서는 email, emailVerified 같은 본인용 필드를 채워서 내려준다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long userId;
    private String username;
    private String nickname;

    private String intro;
    private String profileImageUrl;
    private String backgroundImageUrl;

    private long followerCount;
    private long followingCount;
    private long completedBookCount;

    // /profiles/me 에서만 의미 있는 필드(타인 조회시 null 가능)
    private String email;
    private Boolean emailVerified;

    // TODO: Profile 엔티티에 monthlyGoal 추가되면 사용
    private Integer monthlyGoal;
    private RatingHistogramDto stars;          // 별점 분포
    private Page<ReviewCardDto> reviews;       // 서평 목록 페이지 (옵션)

    public static ProfileResponse from(UserProfileSummaryDto summary,
                                       UserDto me,
                                       Integer monthlyGoal) {
        return from(summary, me, monthlyGoal, null, null);
    }

    public static ProfileResponse from(
            UserProfileSummaryDto summary,
            UserDto user,
            Integer monthlyGoal,
            RatingHistogramDto stars,
            Page<ReviewCardDto> reviews
    ) {
        if (summary == null) {
            return null;
        }

        return ProfileResponse.builder()
                .userId(summary.getUserId())
                .username(summary.getUsername())
                .nickname(summary.getNickname())
                .intro(summary.getIntro())
                .profileImageUrl(summary.getProfileImageUrl())
                .backgroundImageUrl(summary.getBackgroundImageUrl())
                .followerCount(summary.getFollowerCount())
                .followingCount(summary.getFollowingCount())
                .completedBookCount(summary.getCompletedBookCount())
                .email(user != null ? user.getEmail() : null)
                .emailVerified(user != null ? user.isEmailVerified() : null)
                .monthlyGoal(monthlyGoal)
                .stars(stars)
                .reviews(reviews)
                .build();
    }
}
