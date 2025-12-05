package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Profile;
import com.cheack.softwareengineering.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSummaryDto {

    private Long userId;
    private String username;
    private String nickname;

    private String intro;
    private String profileImageUrl;
    private String backgroundImageUrl;

    private long followerCount;
    private long followingCount;
    private long completedBookCount; // COMPLETED 상태 책 개수

    public static UserProfileSummaryDto from(
            User user,
            Profile profile,
            long followerCount,
            long followingCount,
            long completedBookCount
    ) {
        if (user == null) {
            return null;
        }

        return UserProfileSummaryDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .intro(profile != null ? profile.getIntro() : null)
                .profileImageUrl(profile != null ? profile.getUserImage() : null)
                .backgroundImageUrl(profile != null ? profile.getBackgroundImage() : null)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .completedBookCount(completedBookCount)
                .build();
    }
}