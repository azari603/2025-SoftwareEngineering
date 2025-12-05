// src/main/java/com/cheack/softwareengineering/service/SocialSignupService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.SocialSignupToken;
import com.cheack.softwareengineering.exception.CustomException;
import com.cheack.softwareengineering.exception.ErrorCode;
import com.cheack.softwareengineering.repository.SocialSignupTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialSignupService {

    private final SocialSignupTokenRepository socialSignupTokenRepository;

    /** 토큰 유효시간(분) – application.properties에 없으면 기본 10분 */
    @Value("${app.social-signup.exp-minutes:10}")
    private long signupExpMinutes;

    /**
     * 소셜 로그인 성공 직후(신규 유저) → FE로 보낼 signupToken 생성
     */
    @Transactional
    public String createSignupToken(ProviderType provider, String providerId, String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        SocialSignupToken sst = SocialSignupToken.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .token(token)
                .expiresAt(now.plusMinutes(signupExpMinutes))
                .used(false)
                .build();

        socialSignupTokenRepository.save(sst);
        return token;
    }

    /**
     * /auth/social/complete-signup 에서 토큰을 소비(검증+사용처리)할 때 호출
     */
    @Transactional
    public SocialSignupToken consumeSignupToken(String token) {
        SocialSignupToken sst = socialSignupTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_OAUTH2_STATE));

        if (sst.isUsed() || sst.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.SOCIAL_SIGNUP_EXPIRED);
        }

        sst.setUsed(true); // 한 번 쓰면 재사용 불가
        return sst;
    }
}