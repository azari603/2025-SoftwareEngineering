package com.cheack.softwareengineering.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PATCH /profiles/me 요청 바디
 * - nickname, intro 둘 중 일부만 보낼 수 있음
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String nickname;
    private String intro;
}
