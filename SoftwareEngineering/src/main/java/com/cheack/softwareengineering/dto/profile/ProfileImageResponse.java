package com.cheack.softwareengineering.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 이미지 업로드 응답
 * 응답 필드: profileImageUrl
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageResponse {
    private String profileImageUrl;
}
