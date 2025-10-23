package com.example.backend.mail; // 패키지 경로는 본인 프로젝트에 맞게 수정

import java.security.SecureRandom;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final CacheManager cacheManager; // Spring의 CacheManager를 주입받음
    private static final SecureRandom RANDOM = new SecureRandom();
    
    public enum VerificationResult {
        SUCCESS,
        EXPIRED,
        FAILED
    }

    /**
     * 인증번호를 생성하고 이메일로 발송
     */
    public void sendVerificationCode(String email) {
        String verificationCode = createRandomCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Hotel Hub] 이메일 인증 번호 안내");
        message.setText("인증 번호: " + verificationCode);
        mailSender.send(message);

        // 'emailVerificationCodes' 캐시에 이메일을 Key로, 인증번호를 Value로 저장
        Cache cache = cacheManager.getCache("emailVerificationCodes");
        if (cache != null) {
            cache.put(email, verificationCode);
        }
    }

    /**
     * 사용자가 입력한 인증번호를 검증
     */
    public VerificationResult verifyCode(String email, String code) {
        Cache cache = cacheManager.getCache("emailVerificationCodes");
        
        if (cache == null) {
            // 캐시 설정이 없는 경우
            return VerificationResult.FAILED;
        }

        Cache.ValueWrapper valueWrapper = cache.get(email);

        if (valueWrapper == null) {
            // 캐시에 해당 이메일의 코드가 없음 -> 만료되었거나 발송된 적 없음
            return VerificationResult.EXPIRED;
        }

        String storedCode = (String) valueWrapper.get();
        if (storedCode.equals(code)) {
            // 코드가 일치하면 -> 성공
            cache.evict(email);
            return VerificationResult.SUCCESS;
        } else {
            // 코드가 일치하지 않으면 -> 실패
            return VerificationResult.FAILED;
        }
    }
    
    /**
     * 6자리 랜덤 숫자 코드 생성
     */
    private String createRandomCode() {
    	int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}