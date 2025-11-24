// src/main/java/com/cheack/softwareengineering/service/AccountService.java
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
     * (예전에 만든) 순수 Account용 회원가입.
     * 현재는 AuthService.signUp()을 쓰고 있어서 안 써도 된다.
     */
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("email already exists");
        }

        String normUsername = username.trim().toLowerCase();
        String normEmail = email.trim().toLowerCase();

        User user = User.builder()
                .username(normUsername)
                .email(normEmail)
                .password(passwordEncoder.encode(rawPassword))
                .nickname(normUsername)
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
     * 비밀번호 변경 (로그인된 사용자가 자기 비번 바꾸는 용도)
     */
    public void changePassword(String username,
                               String currentPassword,
                               String newPassword) {
        String normUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(normUsername)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + normUsername));

        if (user.getProvider() != ProviderType.LOCAL) {
            throw new IllegalArgumentException("social account cannot change password here");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("current password not matched");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 이메일 인증 메일 재전송 (이메일 기준, 로그인 불필요)
     */
    public void resendVerificationMailByEmail(String email) {
        String normEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normEmail)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + normEmail));

        if (user.getIsEmailVerified()) {
            // 이미 인증된 계정이면 그냥 종료
            return;
        }

        emailService.sendVerificationMail(user.getEmail(), user.getUsername());
    }

    /**
     * username 기준 재전송 – 필요하면 그대로 사용
     */
    public void resendVerificationMail(String username) {
        String normUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(normUsername)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + normUsername));

        if (user.getIsEmailVerified()) {
            return;
        }

        emailService.sendVerificationMail(user.getEmail(), user.getUsername());
    }

    /**
     * 이메일 변경 + 다시 인증 요청
     */
    public void changeEmail(Long userId, String newEmail) {
        String normEmail = newEmail.trim().toLowerCase();

        if (userRepository.existsByEmail(normEmail)) {
            throw new IllegalArgumentException("email already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        user.setEmail(normEmail);
        user.setIsEmailVerified(false);
        userRepository.save(user);

        emailService.sendVerificationMail(normEmail, user.getUsername());
    }

    // ================== 비밀번호 찾기 / 재설정 ==================

    /**
     * 비밀번호 찾기 – 재설정 메일 발송 (AuthController에서 사용)
     */
    public void forgotPassword(String email) {
        String normEmail = email.trim().toLowerCase();
        emailService.sendPasswordResetMail(normEmail);
    }

    /**
     * 비밀번호 찾기 – 재설정 메일 발송 (AccountController에서 사용하던 이름)
     * => 위 forgotPassword() 래핑
     */
    public void requestPasswordReset(String email) {
        forgotPassword(email);
    }

    /**
     * 비밀번호 재설정 – 토큰 검증 후 새 비밀번호로 변경
     */
    public void resetPassword(String token, String newPassword) {
        Long userId = emailService.verifyPasswordResetToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        if (user.getProvider() != ProviderType.LOCAL) {
            throw new IllegalArgumentException("social account cannot reset password here");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ================== 아이디 찾기 ==================

    /**
     * 아이디 찾기 – 이메일로 username 반환
     */
    public String findUsernameByEmail(String email) {
        String normEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normEmail)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + normEmail));
        return user.getUsername();
    }

    // ================== 계정 탈퇴 ==================

    /**
     * 계정 탈퇴 – status=DEACTIVATED (AuthController에서 사용하는 이름)
     */
    public void deleteAccount(String username, String password) {
        String normUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(normUsername)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + normUsername));

        if (user.getProvider() == ProviderType.LOCAL) {
            if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("password not matched");
            }
        }
        // 소셜 계정은 나중에 reauthToken 등으로 검증 로직 추가 가능

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    /**
     * 계정 탈퇴 – AccountController에서 사용하던 이름
     * => deleteAccount() 래핑
     */
    public void deactivateAccount(String username, String password) {
        deleteAccount(username, password);
    }
}