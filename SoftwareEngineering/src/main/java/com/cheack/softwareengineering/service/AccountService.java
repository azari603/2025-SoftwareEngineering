package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.entity.UserStatus;
import com.cheack.softwareengineering.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * 회원 가입
     *  - username / email 중복 체크
     *  - 비밀번호 암호화
     *  - 기본값(LOCAL, ACTIVE 등) 세팅
     *  - 인증 메일 발송
     */
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .nickname(username)                 // 엔티티 주석에 나온 기본 정책
                .provider(ProviderType.LOCAL)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .build();

        User saved = userRepository.save(user);

        // 가입 후 이메일 인증 메일 발송
        emailService.sendVerificationMail(saved.getEmail(), saved.getUsername());

        return saved;
    }

    /**
     * 비밀번호 변경
     *  - 현재 비밀번호 검증
     *  - 새 비밀번호 암호화 후 저장
     */
    public void changePassword(String username,
                               String currentPassword,
                               String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("current password not matched");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 이메일 인증 메일 재발송 요청
     */
    public void resendVerificationMail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + username));

        emailService.sendVerificationMail(user.getEmail(), user.getUsername());
    }

    /**
     * 이메일 인증 토큰 검증 후, 사용자 emailVerified 플래그를 true 로 세팅
     */
    public void verifyEmail(String token) {
        // EmailService 가 토큰 → username 을 검증/반환
        String username = emailService.verifyTokenAndReturnUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("user not found after verification: " + username));

        user.setIsEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * 이메일 변경 요청
     *  - 중복 이메일 체크
     *  - 이메일 변경 후 emailVerified=false 로 초기화
     *  - 새 이메일로 인증 메일 발송
     */
    public void changeEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("email already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        user.setEmail(newEmail);
        user.setIsEmailVerified(false);
        userRepository.save(user);

        emailService.sendVerificationMail(newEmail, user.getUsername());
    }
}
