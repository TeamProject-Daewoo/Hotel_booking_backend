package com.example.backend.coupon.entity;

import com.example.backend.authentication.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    // 쿠폰 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    // 발급일
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    // 사용 여부
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    // 사용일
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    // 쿠폰 만료일
    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "issued_source")
private String issuedSource;  // 예: "신규가입 이벤트", "추석 프로모션"

}
