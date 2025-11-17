package com.example.backend.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    // 1. 인가 코드로 카카오 액세스 토큰 받기
    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUri, kakaoTokenRequest, JsonNode.class);

            // 1. HTTP 상태 코드가 200 OK 인지 확인
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseBody = response.getBody();
                // 2. 응답 본문과 access_token 필드가 존재하는지 확인
                if (responseBody != null && responseBody.has("access_token")) {
                    return responseBody.get("access_token").asText();
                }
            }
            // 200 OK가 아닌 경우 에러 로그 출력
            log.error("카카오 토큰 요청 실패: HTTP Status {}", response.getStatusCode());
            throw new RuntimeException("카카오로부터 access token을 받지 못했습니다.");

        } catch (HttpClientErrorException e) {
            // 4xx, 5xx 에러 발생 시 로그 출력
            log.error("카카오 토큰 요청 중 클라이언트 에러 발생: {}", e.getResponseBodyAsString());
            throw new RuntimeException("카카오 토큰 요청에 실패했습니다.", e);
        }
    }

    // 2. 카카오 액세스 토큰으로 사용자 정보 받기
    public JsonNode getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(null, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(userInfoUri, kakaoProfileRequest, JsonNode.class);
        

        return response.getBody();
    }
}