// src/main/java/com/cheack/softwareengineering/controller/AuthController.java
package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.service.AccountService;
import com.cheack.softwareengineering.service.AuthService;
import com.cheack.softwareengineering.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final UserService userService;

    // ========== 회원가입 / 로그인 / 토큰 ==========

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입 성공. 이메일 인증을 완료해주세요."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        TokenResponse token = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료"));
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
        // 스펙상 202 Accepted 도 가능하지만, 그냥 200 OK 로 통일해도 됨
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("비밀번호 재설정 메일을 전송했습니다."));
    }


    // ========== 아이디 찾기 ==========

    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<FindIdResponse>> findId(@RequestParam String email) {
        String username = accountService.findUsernameByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(new FindIdResponse(username)));
    }

}