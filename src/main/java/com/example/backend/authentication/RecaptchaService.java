package com.example.backend.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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

        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), Map.class);
        
        Map<String, Object> responseBody = response.getBody();
        
        return (Boolean) responseBody.get("success");
    }
}
