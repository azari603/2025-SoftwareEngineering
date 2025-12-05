package com.cheack.softwareengineering.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비밀번호 재설정 요청용 DTO
 * - token: 비밀번호 재설정용 토큰
 * - newPassword: 새 비밀번호
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {

    @NotBlank(message = "토큰은 필수입니다.")
    private String token;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}