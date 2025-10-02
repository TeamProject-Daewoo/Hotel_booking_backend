package com.example.backend.coupon.controller;

import com.example.backend.coupon.dto.CouponDto;
import com.example.backend.coupon.dto.UserCouponDto;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.coupon.entity.UserCoupon;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.authentication.UserRepository;
import com.example.backend.authentication.User; // ← 엔티티 User
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    // [관리자] 전체 쿠폰 조회
    @GetMapping("/list")
    public ResponseEntity<Page<CouponDto>> getAllCouponsForAdmin(Pageable pageable) {
        return ResponseEntity.ok(couponService.getAllCouponsForAdmin(pageable));
    }

    // [운영자] 쿠폰 등록
    @PostMapping
    public ResponseEntity<CouponDto> createCoupon(@RequestBody CouponDto dto) {
        return ResponseEntity.ok(couponService.createCoupon(dto));
    }

    // [운영자/이벤트 페이지] 현재 유효한 쿠폰 목록 조회
    @GetMapping("/valid")
    public ResponseEntity<Page<CouponDto>> getValidCoupons(Pageable pageable) {
        return ResponseEntity.ok(couponService.getValidCoupons(pageable));
    }

@GetMapping("/my")
public ResponseEntity<Page<UserCouponDto>> getMyCoupons(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
        Pageable pageable
) {
    // Security User → 엔티티 User 조회
    User entityUser = userRepository.findByUsername(securityUser.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    return ResponseEntity.ok(
            couponService.getUserCoupons(entityUser.getUsername(), pageable)
    );
}

@GetMapping("/my/available")
public ResponseEntity<Page<UserCouponDto>> getMyAvailableCoupons(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
        Pageable pageable
) {
    User entityUser = userRepository.findByUsername(securityUser.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    return ResponseEntity.ok(
            couponService.getAvailableUserCoupons(entityUser.getUsername(), pageable)
    );
}

@PostMapping("/issue/code")
public ResponseEntity<UserCouponDto> issueCouponByCode(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser,
        @RequestParam String couponCode,
        @RequestParam(required = false, defaultValue = "CODE_INPUT") String source
) {
    CouponDto couponDto = couponService.getCouponByCode(couponCode);

    Coupon coupon = new Coupon();
    coupon.setId(couponDto.getId());

    User entityUser = userRepository.findByUsername(securityUser.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

    return ResponseEntity.ok(
            couponService.issueCouponToUser(entityUser, coupon, source)
    );
}


    // [운영자] 특정 유저에게 수동 발급
    @PostMapping("/issue/manual")
    public ResponseEntity<UserCouponDto> issueCouponManual(
            @RequestParam String username,
            @RequestParam Long couponId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false, defaultValue = "ADMIN") String source
    ) {
        Coupon coupon = new Coupon();
        coupon.setId(couponId);

        // ✅ username 기반으로 우리 엔티티 User 조회
        User entityUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return ResponseEntity.ok(
                couponService.issueCouponToUser(entityUser, coupon, source)
        );
    }

    @DeleteMapping("/user/{id}")
public ResponseEntity<Void> deleteUserCoupon(@PathVariable Long id) {
    userCouponRepository.deleteById(id);
    return ResponseEntity.ok().build();
}

@PatchMapping("/user/{id}/use")
public ResponseEntity<Void> markUserCouponAsUsed(@PathVariable Long id) {
    UserCoupon userCoupon = userCouponRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다. ID: " + id));

    userCoupon.setIsUsed(true);
    userCoupon.setUsedAt(LocalDateTime.now());
    userCouponRepository.save(userCoupon);

    return ResponseEntity.ok().build();
}

@PatchMapping("/user/{id}/cancel")
public ResponseEntity<Void> markUserCouponAsUnused(@PathVariable Long id) {
    UserCoupon userCoupon = userCouponRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다. ID: " + id));

    userCoupon.setIsUsed(false);
    userCoupon.setUsedAt(null);
    userCouponRepository.save(userCoupon);

    return ResponseEntity.ok().build();
}


}