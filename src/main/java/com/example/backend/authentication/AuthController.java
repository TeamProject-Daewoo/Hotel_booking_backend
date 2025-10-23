package com.example.backend.authentication;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.coupon.service.CouponService;
import com.example.backend.mail.MailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final MailService mailService;
	private final KakaoService kakaoService;
	private final CouponService couponService;
	private final RateLimitService rateLimitService;
	
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	// IP 주소를 가져오는 헬퍼 메소드
	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	@PostMapping("/sign-up")
	public ResponseEntity<String> signUp(@RequestBody UserDto.SignUp signUpDto) {
		userService.signUp(signUpDto);
		return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
	}


	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDto.Login loginDto, HttpServletRequest request) {

		String ip = getClientIp(request);

		// 5. (시도 전) IP가 차단되었는지 확인
		if (rateLimitService.isBlocked(ip)) {
			
			log.warn("과도한 로그인 시도 차단. IP: {}", ip);
			
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS) // 429 Too Many Requests
					.body("로그인 시도가 너무 많습니다. 잠시 후 다시 시도해주세요.");
		}

		try {
			
			//로그인 시도시 횟수 깎음
			rateLimitService.recordLoginFailure(ip);
			
			// 1. UserService에서 토큰 정보 받아오기
			TokenInfo tokenInfo = userService.login(loginDto);

			// 2. Refresh Token을 HttpOnly 쿠키로 설정
			ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
					.httpOnly(true)
					// .secure(true) // HTTPS에서만 전송 (배포시에는 주석 해제)
					.path("/").sameSite("Lax").maxAge(60 * 60 * 24 * 7) // 7일
					.build();

			// 3. Access Token만 DTO에 담아 Body로 반환
			UserDto.AccessTokenResponse accessTokenResponse = new UserDto.AccessTokenResponse(
					tokenInfo.getAccessToken());

			return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
					.body(accessTokenResponse);
		} catch (AuthenticationException e) {

			// 401 Unauthorized
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout() {
		// 쿠키의 만료 시간을 0으로 설정하여 삭제
		ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "").maxAge(0).path("/").build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body("로그아웃되었습니다.");
	}

	@PostMapping("/refresh")
	public ResponseEntity<UserDto.AccessTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
		// 쿠키에서 Refresh Token을 가져와 새로운 Access Token 발급
		String newAccessToken = userService.reissueAccessToken(refreshToken);
		UserDto.AccessTokenResponse accessTokenResponse = new UserDto.AccessTokenResponse(newAccessToken);
		return ResponseEntity.ok(accessTokenResponse);
	}

	// 이메일 인증 코드 발송 API
	@PostMapping("/send-verification")
	public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> payload) {
		mailService.sendVerificationCode(payload.get("email"));
		return ResponseEntity.ok("인증 코드가 발송되었습니다.");
	}

	// 이메일 인증 코드 확인 API
	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		String code = payload.get("code");

		// MailService로부터 검증 결과를 받음
		MailService.VerificationResult result = mailService.verifyCode(email, code);

		// 결과에 따라 다른 응답을 반환
		switch (result) {
		case SUCCESS:
			return ResponseEntity.ok("인증이 완료되었습니다.");

		case EXPIRED:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 만료되었습니다.");

		case FAILED:
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 올바르지 않습니다.");
		}
	}

	@PostMapping("/kakao-login")
	public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> payload) {
		String authorizationCode = payload.get("code");

		// 1. 인가 코드로 카카오 액세스 토큰 받기
		String kakaoAccessToken = kakaoService.getKakaoAccessToken(authorizationCode);

		// 2. 카카오 로그인 처리 및 우리 서비스 JWT 발급
		TokenInfo tokenInfo = userService.kakaoLogin(kakaoAccessToken);

		// 3. 응답 생성 (Refresh Token은 쿠키, Access Token은 Body)
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenInfo.getRefreshToken())
				.httpOnly(true).secure(true).path("/").maxAge(60 * 60 * 24 * 7).build();

		UserDto.AccessTokenResponse accessTokenResponse = new UserDto.AccessTokenResponse(tokenInfo.getAccessToken());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(accessTokenResponse);
	}
}
