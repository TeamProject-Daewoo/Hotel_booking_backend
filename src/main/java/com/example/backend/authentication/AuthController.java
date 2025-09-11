package com.example.backend.authentication;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserDto.SignUp signUpDto) {
        userService.signUp(signUpDto);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.AccessTokenResponse> login(@RequestBody UserDto.Login loginDto) {
        // 1. UserService에서 토큰 정보 받아오기
        TokenInfo tokenInfo = userService.login(loginDto);
    
        // 2. Refresh Token을 HttpOnly 쿠키로 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
                .httpOnly(true)
                //.secure(true) // HTTPS에서만 전송 (배포시에는 주석 해제)
                .path("/")
                .sameSite("Lax")
                .maxAge(60 * 60 * 24 * 7) // 7일
                .build();

        // 3. Access Token만 DTO에 담아 Body로 반환
        UserDto.AccessTokenResponse accessTokenResponse = new UserDto.AccessTokenResponse(tokenInfo.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // 쿠키의 만료 시간을 0으로 설정하여 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("로그아웃되었습니다.");
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<UserDto.AccessTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        // 쿠키에서 Refresh Token을 가져와 새로운 Access Token 발급
        String newAccessToken = userService.reissueAccessToken(refreshToken);
        UserDto.AccessTokenResponse accessTokenResponse = new UserDto.AccessTokenResponse(newAccessToken);
        return ResponseEntity.ok(accessTokenResponse);
    }
}