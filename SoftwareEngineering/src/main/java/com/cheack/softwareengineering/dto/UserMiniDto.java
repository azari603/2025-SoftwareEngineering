package com.cheack.softwareengineering.dto;

import com.cheack.softwareengineering.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 서비스/도메인 레이어에서 "유저 요약"으로 쓰는 최소 DTO.
 * 프로필 이미지는 ProfileService 쪽에서 별도 DTO로 붙여서 쓰고,
 * 여기서는 id/username/nickname 정도만 유지한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMiniDto {

    private Long id;
    private String username;
    private String nickname;

    public static UserMiniDto from(User user) {
        if (user == null) {
            return null;
        }
        return UserMiniDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}