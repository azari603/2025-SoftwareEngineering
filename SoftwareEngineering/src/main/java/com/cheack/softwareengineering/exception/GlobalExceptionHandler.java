package com.cheack.softwareengineering.exception;

import com.cheack.softwareengineering.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
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

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .fields(ex.getFields())
                .build();

        HttpStatus status = determineHttpStatus(errorCode);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("BAD_REQUEST")
                .message(ex.getMessage() != null ? ex.getMessage() : "잘못된 요청입니다")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MultipartException.class})
    public ResponseEntity<ErrorResponse> handleMissingPart(Exception ex) {
        String partName = ex instanceof MissingServletRequestPartException
                ? ((MissingServletRequestPartException) ex).getRequestPartName()
                : "file";

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("FILE_REQUIRED")
                .message(partName + " 파트가 필요합니다")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("FILE_TOO_LARGE")
                .message("업로드 용량 제한을 초과했습니다")
                .build();
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponse> handleS3(S3Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("STORAGE_ERROR")
                .message("스토리지 업로드 중 오류가 발생했습니다")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 오류가 발생했습니다")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus determineHttpStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case USER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case DUPLICATE_USERNAME:
            case DUPLICATE_EMAIL:
            case PASSWORD_NOT_MATCH:
            case INVALID_PASSWORD:
            case EMAIL_NOT_VERIFIED:
            case VALIDATION_ERROR:
                return HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN:
            case UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}