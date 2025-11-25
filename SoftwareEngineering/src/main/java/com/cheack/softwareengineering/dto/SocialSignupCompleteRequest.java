package com.cheack.softwareengineering.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialSignupCompleteRequest {

    /** OAuth2SuccessHandler가 리다이렉트할 때 준 소셜 가입용 토큰 */
    @NotBlank
    private String signupToken;

    /** 사용자가 최종 선택한 username (@xxx 같은 거) */
    @NotBlank
    private String username;
}