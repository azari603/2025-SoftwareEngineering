package com.cheack.softwareengineering.dto;

import lombok.*;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private boolean success;
    private String code;
    private String message;
    private Map<String, String> fields;  // validation 에러 필드별 메시지
}