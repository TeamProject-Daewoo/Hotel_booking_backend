package com.example.backend.coupon.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @Column(name = "valid_period_after_download")
    private Integer validPeriodAfterDownload;

    private Integer maxIssuance;

    @Column(nullable = false)
    private Integer issuedCount = 0; // 🔥 기본값 0

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean allowDuplicate = false;

    @Column(unique = true)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuanceType issuanceType;

    public enum IssuanceType {
        AUTO,
        MANUAL,
        EVENT
    }
}
