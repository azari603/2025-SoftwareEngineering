package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.security.JwtProvider;
import com.cheack.softwareengineering.service.AccountService;
import com.cheack.softwareengineering.service.AuthService;
import com.cheack.softwareengineering.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    // ========== 회원가입 / 로그인 / 토큰 ==========

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공. 이메일 인증을 완료해주세요."));
    }

    /**
     * 로그인
     * - 응답 JSON: accessToken 중심(TokenResponse)
     * - refreshToken: HttpOnly 쿠키로 내려감
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);

        // refreshToken을 HttpOnly 쿠키로 세팅
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTPS 환경이면 true로
                .path("/")
                .maxAge(jwtProvider.getRefreshExpSeconds())
                .sameSite("Lax")
                .build();

        // 바디에는 accessToken만 쓰도록 refreshToken을 null로 교체 (FE는 쿠키만 사용)
        TokenResponse bodyToken = new TokenResponse(
                token.getTokenType(),
                token.getAccessToken(),
                null,
                token.getExpiresIn()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(bodyToken));
    }

    /**
     * 토큰 재발급
     * - refreshToken은 HttpOnly 쿠키에서 읽어옴
     * - 새 refreshToken도 쿠키로 재설정
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        TokenResponse token = authService.refreshToken(
                new RefreshTokenRequest(refreshToken) // 필요 시 생성자/빌더에 맞게 수정
        );

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTPS면 true
                .path("/")
                .maxAge(jwtProvider.getRefreshExpSeconds())
                .sameSite("Lax")
                .build();

        TokenResponse bodyToken = new TokenResponse(
                token.getTokenType(),
                token.getAccessToken(),
                null,
                token.getExpiresIn()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(bodyToken));
    }

    /**
     * 로그아웃
     * - refreshToken 쿠키 삭제(Set-Cookie Max-Age=0)
     * - accessToken은 FE에서 버리면 됨
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        // 서버 쪽에서 별도 상태를 안 들고 있으니, 지금은 쿠키만 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.success("로그아웃 완료"));
    }

    // ========== 이메일 인증, 재전송 ==========

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증 완료"));
    }

    @PostMapping("/email/resend")
    public ResponseEntity<ApiResponse<?>> resendVerification(@Valid @RequestBody EmailRequest request) {
        accountService.resendVerificationMailByEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증 이메일 재전송 완료"));
    }

    /**
     * 비로그인 상태에서 이메일 인증 여부 확인
     * GET /api/v1/auth/email/verified?email=...
     * 응답 data: true(인증됨) / false(미인증 or 해당 이메일 없음)
     */
    @GetMapping("/email/verified")
    public ResponseEntity<ApiResponse<Boolean>> isEmailVerified(@RequestParam String email) {
        boolean verified = authService.isEmailVerifiedByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(verified));
    }

    // ========== 중복 체크 ==========

    @GetMapping("/check/username")
    public ResponseEntity<ApiResponse<CheckResponse>> checkUsername(@RequestParam String value) {
        boolean available = !authService.existsByUsername(value);
        String message = available ? "사용 가능한 아이디입니다" : "이미 사용중인 아이디입니다";
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(available, message)));
    }

    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<CheckResponse>> checkEmail(@RequestParam String value) {
        boolean available = !authService.existsByEmail(value);
        String message = available ? "사용 가능한 이메일입니다" : "이미 사용중인 이메일입니다";
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(available, message)));
    }

    // ========== 찾기  ==========
    /** 비밀번호 찾기(메일 발송) */
    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @Valid @RequestBody EmailRequest request
    ) {
        accountService.requestPasswordReset(request.getEmail().trim().toLowerCase());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("비밀번호 재설정 메일을 전송했습니다."));
    }

    // ========== 아이디 찾기 ==========

    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(@RequestParam String email) {
        String username = accountService.findUsernameByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(new FindIdResponse(username)));
    }

    // ========== 소셜 1회 가입 완료 ==========

    @PostMapping("/social/complete-signup")
    public ResponseEntity<ApiResponse<TokenResponse>> completeSocialSignup(
            @Valid @RequestBody SocialSignupCompleteRequest request
    ) {
        TokenResponse tokenResponse = authService.completeSocialSignup(request);

        // 여기서도 새로 발급된 refreshToken을 쿠키로 내려줄 수 있음
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtProvider.getRefreshExpSeconds())
                .sameSite("Lax")
                .build();

        TokenResponse bodyToken = new TokenResponse(
                tokenResponse.getTokenType(),
                tokenResponse.getAccessToken(),
                null,
                tokenResponse.getExpiresIn()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.success(bodyToken));
    }
}