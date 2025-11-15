package com.cheack.softwareengineering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}