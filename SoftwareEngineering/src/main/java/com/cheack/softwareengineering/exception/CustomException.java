package com.cheack.softwareengineering.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> fields;

    /**
     * 기본 생성자 – 추가 필드 없음
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fields = null;
    }

    /**
     * 필드 정보를 함께 담는 생성자
     */
    public CustomException(ErrorCode errorCode, Map<String, String> fields) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fields = fields;
    }

    /**
     * 편의 메서드: 단일 필드만 추가하고 싶을 때 사용
     */
    public static CustomException withField(ErrorCode errorCode, String fieldName, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(fieldName, value);
        return new CustomException(errorCode, map);
    }
}