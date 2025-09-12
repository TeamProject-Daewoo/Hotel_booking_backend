package com.example.backend.authentication;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(UserDto.SignUp signUpDto) {
        if (userRepository.findByUsername(signUpDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
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
}