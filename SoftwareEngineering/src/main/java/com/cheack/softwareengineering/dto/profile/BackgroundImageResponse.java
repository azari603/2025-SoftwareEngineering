package com.cheack.softwareengineering.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배경 이미지 업로드 응답
 * 응답 필드: backgroundImageUrl
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundImageResponse {
    private String backgroundImageUrl;
}
