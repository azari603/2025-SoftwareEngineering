package com.cheack.softwareengineering.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 에러코드
    DUPLICATE_USERNAME("이미 사용중인 아이디입니다."),
    DUPLICATE_EMAIL("이미 사용중인 이메일입니다."),
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),
    PASSWORD_NOT_MATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    ALREADY_VERIFIED("이미 인증된 이메일 입니다."),
    USER_LOCKED("잠긴 계정입니다."),
    UNAUTHORIZED("인증이 필요합니다.");
    private final String message;
}