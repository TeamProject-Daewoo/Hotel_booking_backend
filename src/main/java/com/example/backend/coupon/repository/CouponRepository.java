package com.example.backend.coupon.repository;

import com.example.backend.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCouponCode(String couponCode);

    Page<Coupon> findByIsActiveTrueAndValidFromBeforeAndValidToAfter(
            LocalDateTime now1,
            LocalDateTime now2,
            Pageable pageable
    );

 Optional<Coupon> findFirstByIssuanceTypeAndIsActiveTrueAndValidFromBeforeAndValidToAfter(
        Coupon.IssuanceType issuanceType,
        LocalDateTime now1,
        LocalDateTime now2
);

// CouponRepository.java
@Query("SELECT c FROM Coupon c ORDER BY c.validFrom DESC")
Page<Coupon> findAllCoupons(Pageable pageable);



}
