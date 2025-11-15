package com.cheack.softwareengineering.exception;

import com.cheack.softwareengineering.dto.ApiResponse;
import com.cheack.softwareengineering.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("VALIDATION_ERROR")
                .message("입력값 검증 실패")
                .fields(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code(ex.getErrorCode().name())
                .message(ex.getErrorCode().getMessage())
                .build();

        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(response);
    }

    // 일반 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 오류가 발생했습니다")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // HTTP 상태 코드 결정
    private HttpStatus determineHttpStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case USER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case DUPLICATE_USERNAME:
            case DUPLICATE_EMAIL:
            case PASSWORD_NOT_MATCH:
            case INVALID_PASSWORD:
            case EMAIL_NOT_VERIFIED:
                return HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}