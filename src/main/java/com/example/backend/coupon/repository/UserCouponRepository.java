package com.example.backend.coupon.repository;

import com.example.backend.coupon.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Page<UserCoupon> findByUserUsername(String username, Pageable pageable);

    boolean existsByUserUsernameAndCouponId(String username, Long couponId);

  Page<UserCoupon> findByUserUsernameAndIsUsedFalseAndExpireAtAfterAndCoupon_ValidFromBeforeAndCoupon_ValidToAfter(
    String username,
    LocalDateTime expireAt,   // 유저 쿠폰 만료일 비교용
    LocalDateTime validFrom,  // 쿠폰 시작일
    LocalDateTime validTo,    // 쿠폰 종료일
    Pageable pageable
);

}
