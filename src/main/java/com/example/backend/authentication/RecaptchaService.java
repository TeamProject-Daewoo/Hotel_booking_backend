package com.example.backend.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptchaService {

	@Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyRecaptcha(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        HttpHeaders headers = new HttpHeaders();
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", recaptchaSecretKey);
        body.add("response", token);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);

            // 1. 응답 코드가 200 OK이고, 응답 본문이 null이 아닌지 확인
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // 2. "success" 필드의 값이 정확히 true인지 안전하게 확인
                return Boolean.TRUE.equals(responseBody.get("success"));
            }
            
            // API 호출은 성공했으나 상태 코드가 200이 아닌 경우 (로깅 추천)
            log.warn("reCAPTCHA verification failed with status: {}", response.getStatusCode());
            return false;

        } catch (RestClientException e) {
            // 네트워크 오류 등 RestTemplate 예외 발생 시 (로깅 추천)
            log.error("Error during reCAPTCHA verification request", e);
            return false;
        }
    }
}
