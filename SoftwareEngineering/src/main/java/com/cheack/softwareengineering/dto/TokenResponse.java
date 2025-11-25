package com.cheack.softwareengineering.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String tokenType;     // "Bearer"
    private String accessToken;   // 액세스 토큰
    private String refreshToken;  // 리프레시 토큰 (지금은 아직 바디로 내려보는 상태라면 유지)
    private long expiresIn;       // 초 단위
}