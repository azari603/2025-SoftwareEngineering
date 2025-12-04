package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.security.JwtProvider;
import com.cheack.softwareengineering.service.AccountService;
import com.cheack.softwareengineering.service.AuthService;
import com.cheack.softwareengineering.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공. 이메일 인증을 완료해주세요."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(false)
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

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        TokenResponse token = authService.refreshToken(new RefreshTokenRequest(refreshToken));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(false)
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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
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

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        String successUrl = frontendUrl + "/auth/verify-email?status=success";
        String failUrlBase = frontendUrl + "/auth/verify-email?status=fail&reason=";
        try {
            authService.verifyEmail(token);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, successUrl)
                    .build();
        } catch (Exception e) {
            String reason = URLEncoder.encode("invalid_or_expired", StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, failUrlBase + reason)
                    .build();
        }
    }

    @PostMapping("/email/resend")
    public ResponseEntity<ApiResponse<?>> resendVerification(@Valid @RequestBody EmailRequest request) {
        accountService.resendVerificationMailByEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증 이메일 재전송 완료"));
    }

    @GetMapping("/email/verified")
    public ResponseEntity<ApiResponse<Boolean>> isEmailVerified(@RequestParam String email) {
        boolean verified = authService.isEmailVerifiedByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(verified));
    }

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

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@Valid @RequestBody EmailRequest request) {
        accountService.forgotPassword(request.getEmail().trim().toLowerCase());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("입력하신 이메일로 임시 비밀번호를 전송했습니다. 로그인 후 반드시 비밀번호를 변경해주세요."));
    }

    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(@RequestParam String email) {
        String username = accountService.findUsernameByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(new FindIdResponse(username)));
    }

    @PostMapping("/social/complete-signup")
    public ResponseEntity<ApiResponse<TokenResponse>> completeSocialSignup(
            @Valid @RequestBody SocialSignupCompleteRequest request
    ) {
        TokenResponse tokenResponse = authService.completeSocialSignup(request);

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