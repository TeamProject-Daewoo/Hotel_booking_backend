package com.example.backend.exception; // 예외 패키지

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        // HTTP 409 Conflict 상태 코드와 함께 상세 에러 정보를 JSON으로 반환
        Map<String, String> response = Map.of(
            "message", ex.getMessage(),
            "loginType", ex.getLoginType()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}