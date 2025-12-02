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

    // ================== 이메일 인증 ==================

    @Transactional
    public void sendVerificationMail(String email, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

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

    // ================== 임시 비밀번호 메일 ==================

    /**
     * 비밀번호 찾기 시, 생성된 임시 비밀번호를 메일로 발송
     */
    @Transactional
    public void sendTemporaryPasswordMail(String email, String username, String tempPassword) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[CHEACK] 임시 비밀번호 안내");
            msg.setText(
                    "안녕하세요, " + username + " 님.\n\n"
                            + "요청하신 임시 비밀번호는 다음과 같습니다.\n\n"
                            + tempPassword + "\n\n"
                            + "이 임시 비밀번호로 로그인하신 뒤, 계정 설정 페이지에서 반드시 새 비밀번호로 변경해 주세요."
            );
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("temporary password mail send failed: {}", e.getMessage(), e);
        }
    }
}