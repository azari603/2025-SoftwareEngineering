package com.cheack.softwareengineering.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하입니다")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "아이디는 소문자, 숫자, 언더바만 가능합니다")
    private String username;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*+])[A-Za-z\\d~!@#$%^&*+]{8,20}$",
            message = "비밀번호는 8-20자, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    @AssertTrue(message = "약관에 동의해야 합니다")
    private Boolean agreeTerms;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}