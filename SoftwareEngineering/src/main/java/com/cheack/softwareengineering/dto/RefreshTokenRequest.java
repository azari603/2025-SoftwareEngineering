package com.cheack.softwareengineering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}