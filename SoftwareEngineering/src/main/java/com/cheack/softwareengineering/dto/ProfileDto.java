package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 화면에 내려줄 DTO.
 * 지금은 Profile 엔티티의 필드만 그대로 노출하고,
 * 필요하면 추후에 username, nickname 등을 추가하면 된다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long userId;
    private String userImage;        // 프로필 이미지(URL 또는 스토리지 키)
    private String backgroundImage;  // 배경 이미지(URL 또는 스토리지 키)
    private Long readBook;
    private String intro;

    public static ProfileDto from(Profile profile) {
        if (profile == null) {
            return null;
        }
        return ProfileDto.builder()
                .userId(profile.getUserId())
                .userImage(profile.getUserImage())
                .backgroundImage(profile.getBackgroundImage())
                .readBook(profile.getReadBook())
                .intro(profile.getIntro())
                .build();
    }
}