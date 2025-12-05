package com.cheack.softwareengineering.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 계정 탈퇴 요청용 DTO
 * - password: 본인 인증용 비밀번호
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDeleteRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}