package com.cheack.softwareengineering.service;

import com.cheack.softwareengineering.dto.LoginRequest;
import com.cheack.softwareengineering.dto.SignUpRequest;
import com.cheack.softwareengineering.dto.TokenResponse;
import com.cheack.softwareengineering.dto.SocialSignupCompleteRequest;
import com.cheack.softwareengineering.entity.ProviderType;
import com.cheack.softwareengineering.entity.SocialSignupToken;
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

    // 소셜 회원가입 토큰 관리용
    private final SocialSignupService socialSignupService;

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

    /**
     * refresh 토큰 → 새 access/refresh 발급
     * - refreshToken은 이제 HttpOnly 쿠키에서 읽어와서 여기로 String으로 들어온다고 가정
     */
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String refreshTokenValue) {
        String refresh = refreshTokenValue;

        if (refresh == null || refresh.isBlank()
                || !jwtProvider.validate(refresh)
                || !jwtProvider.isRefreshToken(refresh)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
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
        // 현재 구조에서는 서버에 저장된 세션/토큰이 없으므로 별도 처리 없음.
        // (refresh 토큰은 쿠키 삭제로 처리, access는 FE에서 버림)
    }

    // ================= 소셜 1회 가입 완료 =================

    /**
     * 소셜 1회 가입 완료
     * - FE에서 username, signupToken 넘겨주면
     *   1) signupToken 검증/소비
     *   2) 이메일 충돌 체크
     *   3) username 중복 체크
     *   4) User 생성 또는 기존 temp User 보정
     *   5) 최종 JWT 발급
     */
    @Transactional
    public TokenResponse completeSocialSignup(SocialSignupCompleteRequest request) {
        // 1) 토큰 소비(유효성 + used/만료 체크)
        SocialSignupToken sst = socialSignupService.consumeSignupToken(request.getSignupToken());

        String username = request.getUsername().trim().toLowerCase();

        // 2) 이메일이 기존 계정과 충돌나는지 검사
        userRepository.findByEmail(sst.getEmail()).ifPresent(u -> {
            // 같은 provider+providerId이면 원래 소셜 계정(이 경우 여기까지 안 오는게 정상)
            if (!u.getProvider().equals(sst.getProvider())) {
                throw new CustomException(ErrorCode.OAUTH2_EMAIL_CONFLICT);
            }
        });

        // 3) username 중복 체크
        if (userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        // 4) provider + providerId로 기존 temp User 있는지 확인
        User user = userRepository.findByProviderAndProviderId(sst.getProvider(), sst.getProviderId())
                .orElseGet(() -> {
                    // 혹시라도 첫 로그인 시 User를 안 만들었다면 여기서 새로 생성
                    User created = User.builder()
                            .username(username)
                            .email(sst.getEmail())
                            .password(null)
                            .nickname(username)
                            .emailVerified(true)
                            .provider(sst.getProvider())
                            .providerId(sst.getProviderId())
                            .status(UserStatus.ACTIVE)
                            .build();
                    return userRepository.save(created);
                });

        // temp 이름으로 이미 만들어 둔 경우 → username/nickname 정식으로 교체
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(sst.getEmail());
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        // 5) 최종 JWT 발급
        String access = jwtProvider.generateAccessToken(user.getUsername());
        String refresh = jwtProvider.generateRefreshToken(user.getUsername());

        return new TokenResponse("Bearer", access, refresh, jwtProvider.getAccessExpSeconds());
    }
}