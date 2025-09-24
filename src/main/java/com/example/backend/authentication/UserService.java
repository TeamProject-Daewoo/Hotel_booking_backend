package com.example.backend.authentication;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.exception.UserAlreadyExistsException;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoService kakaoService;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(UserDto.SignUp signUpDto) {
    	userRepository.findByUsername(signUpDto.getUsername()).ifPresent(existingUser -> {
            // 사용자가 이미 존재하면, 로그인 타입을 담아 예외를 발생시킴
            throw new UserAlreadyExistsException(
                "이미 가입된 이메일입니다.", 
                existingUser.getLoginType() == null ? "이메일" : existingUser.getLoginType()
            );
        });
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        User user = signUpDto.toEntity(encodedPassword);
        userRepository.save(user);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenInfo login(UserDto.Login loginDto) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        return tokenInfo;
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Refresh Token에서 username 가져오기
        String username = jwtTokenProvider.getUsername(refreshToken);

        // 3. DB에서 사용자 정보 조회
        UserDetails userDetails = loadUserByUsername(username);

        // 4. 새로운 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        // 5. 새로운 Access Token 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // (선택) DB/Redis에 Refresh Token 업데이트 로직 추가
        
        return tokenInfo.getAccessToken();
    }

    /**
     * Spring Security가 사용자를 인증할 때 사용하는 메소드
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다."));
        
        // ✨ 로그인 시 승인 상태 검증
        if (!user.isAccountNonLocked()) {
            throw new LockedException("아직 승인되지 않은 계정입니다.");
        }

        return user;
    }

    public UserDto.Info getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다."));
        return UserDto.Info.from(user);
    }
    
    public TokenInfo kakaoLogin(String kakaoAccessToken) {
    	
        // 1. 카카오 액세스 토큰으로 사용자 정보 가져오기
        JsonNode userInfo = kakaoService.getUserInfo(kakaoAccessToken);
        String kakaoId = userInfo.path("id").asText(null);
        System.out.println(userInfo);
        String nickname = userInfo
                .path("kakao_account")
                .path("profile")
                .path("nickname")
                .asText(null);
        String phone_number = userInfo.path("kakao_account").path("phone_number").asText(null);
        String email = userInfo.path("kakao_account").path("email").asText(null);
        
        phone_number = formatPhoneNumber(phone_number);
        
        Optional<User> existingUserByEmail = userRepository.findByUsername(email);
     // 👇 이 if문을 수정합니다.
        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();
            
            // 이메일이 중복되면서, 동시에 기존 계정의 loginType이 null(일반 가입)인지 확인
            if (user.getLoginType() == null || user.getLoginType().equals("NORMAL")) {
                throw new UserAlreadyExistsException(
                    "이미 가입된 이메일입니다.", 
                    "이메일"
                );
            }
        }

        // 2. DB에 해당 사용자가 없으면 새로 가입 처리
        User user = userRepository.findByUuid(kakaoId).orElse(null);
        if (user == null) {
            user = User.builder()
                    .username(email) // 임시 username
                    .name(nickname)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 임시 비밀번호
                    .uuid(kakaoId)
                    .phoneNumber(phone_number)
                    .loginType("KAKAO")
                    .role(Role.USER) // 기본 역할
                    .build();
            userRepository.save(user);
        }
        
        UserDetails userDetails = loadUserByUsername(email);

        // 3. 우리 서비스의 JWT 토큰 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), user.getAuthorities());
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        return tokenInfo;
    }
    
    public String formatPhoneNumber(String phoneNumber) {
        // 국가 코드 "+82"을 "010"으로 변환
        if (phoneNumber.startsWith("+82")) {
            return "010" + phoneNumber.substring(6);  // "+82"를 제거하고 "010"으로 대체
        }
        return phoneNumber;  // 이미 "010"이면 그대로 반환
    }
}