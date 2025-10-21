package com.example.backend.authentication;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bucket;

@Service
public class RateLimitService {
	// IP 주소를 키로 사용하여 로그인 시도 횟수를 관리하는 캐시
    private final Cache<String, Bucket> loginCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10)) // 10분간 활동 없으면 캐시에서 제거
            .maximumSize(10_000)
            .build();

    // 로그인 시도 횟수를 관리하는 Bucket 생성
    private Bucket newLoginBucket() {
        return Bucket.builder()
                .addLimit(
                    limit ->
                        // 10번의 실패 기회 (Capacity)
                        limit.capacity(5)
                             // 1분에 5개씩 기회 복원 (Refill)
                            .refillGreedy(2, Duration.ofMinutes(1))
                )
                .build();
    }

    /**
     * (로그인 시도 전) 해당 IP가 차단되었는지 확인합니다.
     * @param ip 사용자 IP
     * @return 차단되었으면 true
     */
    public boolean isBlocked(String ip) {
        Bucket bucket = loginCache.get(ip, k -> newLoginBucket());
        // 남은 시도 가능 횟수(토큰)가 0개인지 확인합니다.
        return bucket.getAvailableTokens() == 0;
    }

    /**
     * (로그인 실패 시) 해당 IP의 시도 횟수를 1 차감합니다.
     * @param ip 사용자 IP
     */
    public void recordLoginFailure(String ip) {
        Bucket bucket = loginCache.get(ip, k -> newLoginBucket());
        bucket.tryConsume(1);
    }
}
