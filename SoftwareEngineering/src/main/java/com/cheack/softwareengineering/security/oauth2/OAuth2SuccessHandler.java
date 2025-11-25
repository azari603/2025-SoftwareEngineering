// src/main/java/com/cheack/softwareengineering/security/oauth2/OAuth2SuccessHandler.java
package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.security.JwtProvider;
import com.cheack.softwareengineering.service.SocialSignupService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final SocialSignupService socialSignupService;

    // FE 리다이렉트 기본 URL (리액트)
    private static final String REDIRECT_URL = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // "첫 소셜 로그인" 판단 기준: 임시 아이디 정책 (예: temp_ / @something)
        boolean isNewUser = user.getUsername().startsWith("temp_") || user.getUsername().startsWith("@");

        String targetUrl;

        if (isNewUser) {
            // 🔹 소셜 최초 유저: 1회용 signupToken 발급
            String signupToken = socialSignupService.createSignupToken(
                    user.getProvider(),   // ProviderType (GOOGLE / KAKAO / NAVER)
                    user.getProviderId(), // 소셜 고유 id
                    user.getEmail()       // 이메일
            );

            targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                    .queryParam("mode", "signup")
                    .queryParam("signupToken", signupToken)
                    .build()
                    .toUriString();
        } else {
            // 🔹 기존 유저: 바로 JWT 발급해서 로그인 완료로 보냄
            String accessToken = jwtProvider.createAccessToken(user.getUsername());
            String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

            targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                    .queryParam("mode", "login")
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build()
                    .toUriString();
        }

        response.sendRedirect(targetUrl);
    }
}