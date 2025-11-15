package com.cheack.softwareengineering.security.oauth2;

import com.cheack.softwareengineering.security.JwtProvider;
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
    // 리액트
    private final String REDIRECT_URL = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 첫 소셜 로그인인지 체크
        boolean isNewUser = oAuth2User.getUser().getUsername().startsWith("temp_");

        String token = jwtProvider.createAccessToken(oAuth2User.getUser().getUsername());
        String refreshToken = jwtProvider.createRefreshToken(oAuth2User.getUser().getUsername());

        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .queryParam("isNewUser", isNewUser)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}