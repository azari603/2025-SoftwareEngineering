package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.entity.UserStatus;
import com.cheack.softwareengineering.exception.CustomException;
import com.cheack.softwareengineering.exception.ErrorCode;
import com.cheack.softwareengineering.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private String generateTemporaryPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        int n = TEMP_PASSWORD_CHARS.length();
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(n);
            sb.append(TEMP_PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    /**
     * (예전에 만든) 순수 Account용 회원가입.
     * 현재는 AuthService.signUp()을 쓰고 있어서 안 써도 된다.
     */
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProvider() != ProviderType.LOCAL) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getIsEmailVerified()) {
            return;
        }

        emailService.sendVerificationMail(user.getEmail(), user.getUsername());
    }

    /**
     * username 기준 재전송
     */
    public void resendVerificationMail(String username) {
        String normUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(normUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setEmail(normEmail);
        user.setIsEmailVerified(false);
        userRepository.save(user);

        emailService.sendVerificationMail(normEmail, user.getUsername());
    }

    // ================== 비밀번호 찾기 (임시 비밀번호 발급) ==================

    /**
     * 비밀번호 찾기
     * 1. 이메일로 User 조회
     * 2. ProviderType.LOCAL이 아닌 경우 에러
     * 3. 임시 비밀번호 생성 후 DB에 저장
     * 4. 해당 임시 비밀번호를 메일로 발송
     */
    public void forgotPassword(String email) {
        String normEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProvider() != ProviderType.LOCAL) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String tempPassword = generateTemporaryPassword(12);

        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        emailService.sendTemporaryPasswordMail(
                normEmail,
                user.getUsername(),
                tempPassword
        );
    }

    /**
     * 예전 이름 호환용 래퍼
     */
    public void requestPasswordReset(String email) {
        forgotPassword(email);
    }

    // ================== 아이디 찾기 ==================

    public String findUsernameByEmail(String email) {
        String normEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(normEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getUsername();
    }

    // ================== 계정 탈퇴 ==================

    public void deleteAccount(String username, String password) {
        String normUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(normUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProvider() == ProviderType.LOCAL) {
            if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public void deactivateAccount(String username, String password) {
        deleteAccount(username, password);
    }
}