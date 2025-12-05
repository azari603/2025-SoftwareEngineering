// src/main/java/com/cheack/softwareengineering/security/oauth2/OAuth2SuccessHandler.java
package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.security.JwtProvider;
import com.cheack.softwareengineering.service.SocialSignupService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    private static final String REDIRECT_URL = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        boolean isNewUser = user.getUsername().startsWith("temp_") || user.getUsername().startsWith("@");

        String targetUrl;

        if (isNewUser) {
            // 신규 소셜 유저: 1회용 signupToken만 쿼리로 전달
            String signupToken = socialSignupService.createSignupToken(
                    user.getProvider(),
                    user.getProviderId(),
                    user.getEmail()
            );

            targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                    .queryParam("mode", "signup")
                    .queryParam("signupToken", signupToken)
                    .build()
                    .toUriString();

        } else {
            // 기존 유저: access/refresh 모두 HttpOnly 쿠키로만 내려주고
            // 쿼리에는 토큰을 전혀 안 보냄
            String accessToken = jwtProvider.createAccessToken(user.getUsername());
            String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtProvider.getAccessExpSeconds())
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtProvider.getRefreshExpSeconds())
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                    .queryParam("mode", "login")
                    .build()
                    .toUriString();
        }

        response.sendRedirect(targetUrl);
    }
}