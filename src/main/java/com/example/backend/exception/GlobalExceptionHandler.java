package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 405 Method Not Allowed 예외 처리
     * (예: GET을 지원하지 않는 API에 GET 요청을 보낸 경우)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        
        Map<String, String> response = Map.of(
            "error", "Method Not Allowed",
            "message", "요청하신 페이지는 지원되지 않습니다."
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        
        // 🚨 실제 운영 환경에서는 e.getMessage()를 노출하지 않는 것이 좋습니다.
        Map<String, String> response = Map.of(
            "error", "Internal Server Error",
            "message", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."
        );
        
        // 서버 콘솔에는 실제 오류를 기록
        ex.printStackTrace(); 
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}