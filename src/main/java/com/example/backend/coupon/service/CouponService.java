    package com.example.backend.coupon.service;

    import com.example.backend.authentication.User;
    import com.example.backend.authentication.UserRepository;
    import com.example.backend.coupon.dto.CouponDto;
    import com.example.backend.coupon.dto.UserCouponDto;
    import com.example.backend.coupon.entity.Coupon;
    import com.example.backend.coupon.entity.UserCoupon;
    import com.example.backend.coupon.repository.CouponRepository;
    import com.example.backend.coupon.repository.UserCouponRepository;
    import lombok.RequiredArgsConstructor;

    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class CouponService {

        private final UserRepository userRepository;
        private final CouponRepository couponRepository;
        private final UserCouponRepository userCouponRepository;

        // 쿠폰 코드로 쿠폰 찾기
        public CouponDto getCouponByCode(String couponCode) {
            Coupon coupon = couponRepository.findByCouponCode(couponCode)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다."));
            return CouponDto.fromEntity(coupon);
        }

        // 유효한 쿠폰 목록 조회
        public Page<CouponDto> getValidCoupons(Pageable pageable) {
            LocalDateTime now = LocalDateTime.now();
            return couponRepository
                    .findByIsActiveTrueAndValidFromBeforeAndValidToAfter(now, now, pageable)
                    .map(CouponDto::fromEntity);
        }

        // 관리자용 전체 쿠폰 조회
        public Page<CouponDto> getAllCouponsForAdmin(Pageable pageable) {
            return couponRepository.findAllCoupons(pageable)
                    .map(CouponDto::fromEntity);
        }

        // 특정 유저의 쿠폰함 조회
        public Page<UserCouponDto> getUserCoupons(String username, Pageable pageable) {
            return userCouponRepository
                    .findByUserUsername(username, pageable)
                    .map(UserCouponDto::fromEntity);
        }

        // 사용 가능한 쿠폰만 조회
        public Page<UserCouponDto> getAvailableUserCoupons(String username, Pageable pageable) {
            LocalDateTime now = LocalDateTime.now();
            return userCouponRepository
                    .findByUserUsernameAndIsUsedFalseAndExpireAtAfterAndCoupon_ValidFromBeforeAndCoupon_ValidToAfter(
                            username, now, now, now, pageable
                    )
                    .map(UserCouponDto::fromEntity);
        }

        // CouponService.java
   @Transactional
public UserCouponDto issueCouponToUser(User user, Coupon coupon, String source) {
    coupon = couponRepository.findById(coupon.getId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

             LocalDateTime now = LocalDateTime.now();

               // 신규 회원만 발급 가능 (AUTO 전용)
    if (coupon.getIssuanceType() == Coupon.IssuanceType.AUTO && !user.isNewUser()) {
        throw new IllegalStateException("신규 회원만 발급 가능한 쿠폰입니다.");
    }

    // ✅ 추가 부분: 아직 시작 전 쿠폰이면 발급 불가
    if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(now)) {
        throw new IllegalStateException("아직 사용 시작 전인 쿠폰입니다.");
    }

    // ✅ 추가 부분: 이미 만료된 쿠폰이면 발급 불가
if (coupon.getValidTo() != null && coupon.getValidTo().isBefore(now)) {
    throw new IllegalStateException("이미 만료된 쿠폰입니다.");
}

    // 중복 발급 방지
    if (!coupon.getAllowDuplicate()) {
        boolean exists = userCouponRepository.existsByUserUsernameAndCouponId(user.getUsername(), coupon.getId());
        if (exists) throw new IllegalStateException("이미 발급된 쿠폰입니다.");
    }

    // 발급 수량 체크
    if (coupon.getMaxIssuance() != null &&
            coupon.getIssuedCount() >= coupon.getMaxIssuance()) {
        throw new IllegalStateException("쿠폰 발급 수량이 모두 소진되었습니다.");
    }

    // ✅ 할인 값 fallback 처리
    Integer discountAmount = coupon.getDiscountAmount();
    Integer discountPercent = coupon.getDiscountPercent();

    // 금액/퍼센트 둘 다 없으면 기본 금액 1000원으로 설정
    if ((discountAmount == null || discountAmount == 0) && 
        (discountPercent == null || discountPercent == 0)) {
        discountAmount = 1000;
        coupon.setDiscountAmount(1000);
        coupon.setDiscountPercent(0);
    }

    // 만료일 계산
    LocalDateTime expireAt;
    if (coupon.getValidPeriodAfterDownload() != null) {
        expireAt = LocalDateTime.now().plusDays(coupon.getValidPeriodAfterDownload());
    } else {
        expireAt = LocalDateTime.now().plusDays(7); // 기본 7일
    }

    UserCoupon userCoupon = UserCoupon.builder()
            .user(user)
            .coupon(coupon)
            .issuedAt(LocalDateTime.now())
            .expireAt(expireAt)
            .isUsed(false)
            .issuedSource(source)
            .build();

    // 발급 카운트 증가
    coupon.setIssuedCount(Optional.ofNullable(coupon.getIssuedCount()).orElse(0) + 1);
    couponRepository.save(coupon);

    return UserCouponDto.fromEntity(userCouponRepository.save(userCoupon));
}


    @Transactional
    public CouponDto createCoupon(CouponDto dto) {
        Coupon coupon = Coupon.builder()
                .name(dto.getName())
                .couponCode(dto.getCouponCode())
                .discountAmount(dto.getDiscountAmount())
                .discountPercent(dto.getDiscountPercent())
                .validFrom(dto.getValidFrom())
                .validTo(dto.getValidTo())
                .validPeriodAfterDownload(dto.getValidPeriodAfterDownload())
                // 🔥 0이면 null 처리
                .maxIssuance(dto.getMaxIssuance() != null && dto.getMaxIssuance() == 0 ? null : dto.getMaxIssuance())
                .issuedCount(dto.getIssuedCount() != null ? dto.getIssuedCount() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .allowDuplicate(dto.getAllowDuplicate() != null ? dto.getAllowDuplicate() : false)
                .issuanceType(dto.getIssuanceType())
                .build();
        Coupon saved = couponRepository.save(coupon);
        return CouponDto.fromEntity(saved);
    }

        // 웰컴 쿠폰 자동 발급
    @Transactional
    public void issueWelcomeCoupon(User user) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Coupon> welcomeCoupon = couponRepository
                .findFirstByIssuanceTypeAndIsActiveTrueAndValidFromBeforeAndValidToAfter(
                        Coupon.IssuanceType.AUTO, now, now
                );

        if (welcomeCoupon.isEmpty()) return;

        Coupon coupon = welcomeCoupon.get();

        if (!coupon.getAllowDuplicate()) {
            boolean exists = userCouponRepository.existsByUserUsernameAndCouponId(user.getUsername(), coupon.getId());
            if (exists) return;
        }

        // ✅ 발급 시 기본 할인값 포함
        issueCouponToUser(user, coupon, "AUTO");
    }
    }
