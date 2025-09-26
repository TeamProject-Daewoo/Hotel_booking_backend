package com.example.backend.coupon.dto;

import com.example.backend.coupon.entity.UserCoupon;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponDto {

    private Long id;
    private String username;
    private CouponDto coupon;
    private Boolean isUsed;
    private LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private LocalDateTime expireAt;
    private String issuedSource;

    public static UserCouponDto fromEntity(UserCoupon userCoupon) {
        return UserCouponDto.builder()
                .id(userCoupon.getId())
                .username(userCoupon.getUser().getUsername())
                .coupon(CouponDto.fromEntity(userCoupon.getCoupon()))
                .isUsed(userCoupon.getIsUsed())
                .issuedAt(userCoupon.getIssuedAt())
                .usedAt(userCoupon.getUsedAt())
                .expireAt(userCoupon.getExpireAt())
                .issuedSource(userCoupon.getIssuedSource())
                .build();
    }
}
