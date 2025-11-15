package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.LoginRequest;
import com.cheack.softwareengineering.dto.RefreshTokenRequest;
import com.cheack.softwareengineering.dto.SignUpRequest;
import com.cheack.softwareengineering.dto.TokenResponse;
import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.entity.UserStatus;
import com.cheack.softwareengineering.exception.CustomException;
import com.cheack.softwareengineering.exception.ErrorCode;
import com.cheack.softwareengineering.repository.UserRepository;
import com.cheack.softwareengineering.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(SignUpRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .username(req.getUsername().trim().toLowerCase())
                .email(req.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getUsername().trim().toLowerCase())
                .emailVerified(false)
                .provider(ProviderType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        emailService.sendVerificationMail(user.getEmail(), user.getUsername());
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username.trim().toLowerCase());
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername().trim().toLowerCase())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if (!user.getIsEmailVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new CustomException(ErrorCode.USER_LOCKED);
        }

        String access = jwtProvider.generateAccessToken(user.getUsername());
        String refresh = jwtProvider.generateRefreshToken(user.getUsername());
        return new TokenResponse("Bearer", access, refresh, jwtProvider.getAccessExpSeconds());
    }

    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest req) {
        String refresh = req.getRefreshToken();
        if (refresh == null || !jwtProvider.validate(refresh) || !jwtProvider.isRefreshToken(refresh)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 필요시 INVALID_TOKEN 정의
        }
        String username = jwtProvider.extractUsername(refresh);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new CustomException(ErrorCode.USER_LOCKED);
        }
        String newAccess = jwtProvider.generateAccessToken(username);
        String newRefresh = jwtProvider.generateRefreshToken(username); // 회전 방식
        return new TokenResponse("Bearer", newAccess, newRefresh, jwtProvider.getAccessExpSeconds());
    }

    @Transactional
    public void verifyEmail(String token) {
        String username = emailService.verifyTokenAndReturnUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setIsEmailVerified(true);
    }

    @Transactional
    public void logout(String token) {
        // TODO: 리프레시 블랙리스트/저장소 연계(선택). 현재는 No-Op.
    }
}