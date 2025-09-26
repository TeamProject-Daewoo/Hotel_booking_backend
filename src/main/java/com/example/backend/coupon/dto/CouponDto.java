package com.example.backend.coupon.dto;

import com.example.backend.coupon.entity.Coupon;
import lombok.*;
import java.util.Optional;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {

    private Long id;
    private String name;
    private Integer discountAmount;
    private Integer discountPercent;
    private String displayDiscount; // 추가: 프론트 표시용
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer validPeriodAfterDownload;
    private Integer maxIssuance;
    private Integer issuedCount;
    private Boolean isActive;
    private Boolean allowDuplicate;
    private String couponCode;
    private Coupon.IssuanceType issuanceType;

    public static CouponDto fromEntity(Coupon coupon) {
        int amount = Optional.ofNullable(coupon.getDiscountAmount()).orElse(0);
        int percent = Optional.ofNullable(coupon.getDiscountPercent()).orElse(0);

        // displayDiscount 계산
        String displayDiscount;
        if (percent > 0) {
            displayDiscount = percent + "% 할인";
        } else if (amount > 0) {
            displayDiscount = amount + "원 할인";
        } else {
            displayDiscount = "0원 할인";
        }

        return CouponDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .discountAmount(amount)
                .discountPercent(percent)
                .displayDiscount(displayDiscount) // 여기 반영
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .validPeriodAfterDownload(coupon.getValidPeriodAfterDownload())
                .maxIssuance(coupon.getMaxIssuance())
                .issuedCount(coupon.getIssuedCount())
                .isActive(coupon.getIsActive())
                .allowDuplicate(coupon.getAllowDuplicate())
                .couponCode(coupon.getCouponCode())
                .issuanceType(coupon.getIssuanceType())
                .build();
    }
}
