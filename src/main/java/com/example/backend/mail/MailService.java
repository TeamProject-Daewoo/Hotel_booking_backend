package com.example.backend.mail; // 패키지 경로는 본인 프로젝트에 맞게 수정

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final CacheManager cacheManager; // Spring의 CacheManager를 주입받음

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
    public boolean verifyCode(String email, String code) {
        Cache cache = cacheManager.getCache("emailVerificationCodes");
        if (cache != null && cache.get(email) != null) {
            String storedCode = cache.get(email, String.class);
            if (storedCode.equals(code)) {
                // 인증 성공 시 캐시에서 코드 삭제 (재사용 방지)
                cache.evict(email);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 6자리 랜덤 숫자 코드 생성
     */
    private String createRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}