package com.cheack.softwareengineering.controller;

import com.cheack.softwareengineering.dto.*;
import com.cheack.softwareengineering.entity.User;
import com.cheack.softwareengineering.exception.CustomException;
import com.cheack.softwareengineering.exception.ErrorCode;
import com.cheack.softwareengineering.service.AccountService;
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
public class AccountController {

    private final AccountService accountService;
    private final UserService userService; // 이미 있는 UserService 사용 (UserDto 조회용)

    /** 비밀번호 변경 (로그인 상태) */
    @PostMapping("/password/change")
    public ResponseEntity<ApiResponse<?>> changePassword(
            Authentication auth,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        if (auth == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        String username = auth.getName();
        accountService.changePassword(username,
                request.getCurrentPassword(),
                request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }

    /** 비밀번호 재설정 (토큰 + 새 비밀번호) */
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        accountService.resetPassword(request.getToken(), request.getNewPassword());
        // 바디 없이 204 도 가능하지만, 통일성 위해 메시지 반환
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다."));
    }

    // ========== 내 계정 조회 / 탈퇴 ==========
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me(Authentication authentication) {
        String username = authentication.getName();
        UserDto dto = userService.getByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @Valid @RequestBody AccountDeleteRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        accountService.deleteAccount(username, request.getPassword());
        return ResponseEntity.noContent().build();
    }
}
