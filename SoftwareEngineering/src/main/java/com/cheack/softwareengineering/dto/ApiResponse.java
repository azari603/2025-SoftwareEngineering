package com.cheack.softwareengineering.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static ApiResponse<?> success(String message) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .build();
    }
}