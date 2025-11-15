package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 완료"));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
//        TokenResponse token = authService.refreshToken(request.getRefreshToken());
//        return ResponseEntity.ok(ApiResponse.success(token));
//    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증 완료"));
    }

//    @PostMapping("/send-verification")
//    public ResponseEntity<ApiResponse<?>> resendVerification(@RequestBody EmailRequest request) {
//        authService.sendVerificationEmail(request.getEmail());
//        return ResponseEntity.ok(ApiResponse.success("인증 이메일 전송 완료"));
//    }

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
}