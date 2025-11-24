// src/main/java/com/cheack/softwareengineering/service/EmailService.java
package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.EmailVerificationToken;
import com.cheack.softwareengineering.entity.PasswordResetToken;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.repository.EmailVerificationTokenRepository;
import com.cheack.softwareengineering.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email-verification.exp-minutes:30}")
    private long expMinutes;

    @Value("${app.email-verification.cooldown-seconds:60}")
    private long cooldownSeconds;

    @Value("${app.password-reset.exp-minutes:30}")
    private long resetExpMinutes;

    @Value("${app.password-reset.cooldown-seconds:60}")
    private long resetCooldownSeconds;

    // ================== 이메일 인증 ==================

    /** 인증 메일 전송 */
    @Transactional
    public void sendVerificationMail(String email, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        // 최근 발급 쿨다운
        final boolean[] blocked = {false};
        tokenRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(latest -> {
            if (latest.getCreatedAt() != null &&
                    latest.getCreatedAt().plusSeconds(cooldownSeconds).isAfter(LocalDateTime.now())) {
                log.warn("verification mail cooldown - username={}, last={}", username, latest.getCreatedAt());
                blocked[0] = true;
            }
        });
        if (blocked[0]) {
            return;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime exp = LocalDateTime.now().plusMinutes(expMinutes);

        EmailVerificationToken evt = new EmailVerificationToken();
        evt.setUser(user);
        evt.setToken(token);
        evt.setExpiresAt(exp);
        evt.setUsed(false);
        tokenRepository.save(evt);

        // 실제 컨트롤러 경로: GET /api/v1/auth/verify-email?token=...
        String link = baseUrl + "/api/v1/auth/verify-email?token=" + token;

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[CHEACK] 이메일 인증 안내");
            msg.setText("다음 링크를 클릭하여 이메일 인증을 완료하세요.\n\n"
                    + link + "\n\n유효기간: " + expMinutes + "분");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("mail send failed: {}", e.getMessage(), e);
        }
    }

    /** 이메일 인증 토큰 검증 후 username 반환(단건 사용) */
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

    // ================== 비밀번호 재설정 ==================

    /** 비밀번호 재설정 메일 전송 */
    @Transactional
    public void sendPasswordResetMail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + email));

        // 최근 발급 쿨다운
        final boolean[] blocked = {false};
        passwordResetTokenRepository.findTopByUserOrderByCreatedAtDesc(user).ifPresent(latest -> {
            if (latest.getCreatedAt() != null &&
                    latest.getCreatedAt().plusSeconds(resetCooldownSeconds).isAfter(LocalDateTime.now())) {
                log.warn("password reset cooldown - email={}, last={}", email, latest.getCreatedAt());
                blocked[0] = true;
            }
        });
        if (blocked[0]) {
            return;
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime exp = LocalDateTime.now().plusMinutes(resetExpMinutes);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setToken(token);
        prt.setExpiresAt(exp);
        prt.setUsed(false);
        passwordResetTokenRepository.save(prt);

        // 이 링크는 FE 전용: /reset-password?token=... (프론트에서 이 토큰으로 /password/reset 호출)
        String link = baseUrl + "/reset-password?token=" + token;

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[CHEACK] 비밀번호 재설정 안내");
            msg.setText("다음 링크를 클릭하여 비밀번호를 재설정하세요.\n\n"
                    + link + "\n\n유효기간: " + resetExpMinutes + "분");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("password reset mail send failed: {}", e.getMessage(), e);
        }
    }

    /** 비밀번호 재설정 토큰 검증 후 userId 반환(단건 사용) */
    @Transactional
    public Long verifyPasswordResetToken(String token) {
        PasswordResetToken prt = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("invalid token"));

        if (prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            prt.setUsed(true);
            passwordResetTokenRepository.save(prt);
            throw new IllegalArgumentException("expired token");
        }

        prt.setUsed(true);
        passwordResetTokenRepository.save(prt);
        return prt.getUser().getId();
    }
}