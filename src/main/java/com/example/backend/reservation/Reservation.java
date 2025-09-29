package com.example.backend.reservation;

import com.example.backend.api.Hotels;
import com.example.backend.authentication.User;
import com.example.backend.coupon.entity.Coupon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp; // 추가

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentid")
    private Hotels hotel;

    private String roomcode;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "num_adults")
    private Integer numAdults;

    @Column(name = "num_children")
    private Integer numChildren;

    private String status;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "base_price")
    private Integer basePrice;

    @Column(name = "discount_price")
    private Integer discountPrice;
    
    @Column(name = "reserv_name")
    private String reservName;
    
    @Column(name = "reserv_phone")
    private String reservPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_coupon_id")
    private Coupon usedCoupon;

    @Column(name = "used_points")
    private Integer usedPoints;

    @CreationTimestamp // 추가
    @Column(name = "reservation_date", updatable = false) // insertable=false 제거
    private LocalDateTime reservationDate;
}