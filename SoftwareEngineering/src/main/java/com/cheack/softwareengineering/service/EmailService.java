package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.EmailVerificationToken;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.EmailVerificationTokenRepository;
import com.cheack.softwareengineering.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email-verification.exp-minutes:30}")
    private long expMinutes;

    @Value("${app.email-verification.cooldown-seconds:60}")
    private long cooldownSeconds;

    /** 인증 메일 전송 */
    @Transactional
    public void sendVerificationMail(String email, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        // 최근 발급 쿨다운(선택)
        tokenRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(latest -> {
            if (latest.getCreatedAt() != null &&
                    latest.getCreatedAt().plusSeconds(cooldownSeconds).isAfter(LocalDateTime.now())) {
                log.warn("verification mail cooldown - username={}, last={}", username, latest.getCreatedAt());
                // 쿨다운 위반이어도 개발 단계에선 그냥 새 토큰 발급하지 않고 조용히 종료
                return;
            }
        });

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime exp = LocalDateTime.now().plusMinutes(expMinutes);

        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setUser(user);
        evt.setToken(token);
        evt.setExpiresAt(exp);
        evt.setUsed(false);
        tokenRepository.save(evt);

        String link = baseUrl + "/api/auth/email/verify?token=" + token;

        // 단순 텍스트 메일
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[CHEACK] 이메일 인증 안내");
            msg.setText("다음 링크를 클릭하여 이메일 인증을 완료하세요.\n\n" + link + "\n\n" +
                    "유효기간: " + expMinutes + "분");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("mail send failed: {}", e.getMessage(), e);
            // 메일 실패는 개발 단계에서는 예외 전파하지 않음(원하면 throw로 변경)
        }
    }

    /** 토큰 검증 후 username 반환(단건사용) */
    @Transactional
    public String verifyTokenAndReturnUsername(String token) {
        EmailVerificationToken evt = tokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("invalid token"));

        if (evt.getExpiresAt().isBefore(LocalDateTime.now())) {
            evt.setUsed(true);
            tokenRepository.save(evt);
            throw new IllegalArgumentException("expired token");
        }

        evt.setUsed(true);
        tokenRepository.save(evt);

        User user = evt.getUser();
        return Optional.ofNullable(user.getUsername())
                .orElseThrow(() -> new IllegalStateException("user has no username"));
    }
}