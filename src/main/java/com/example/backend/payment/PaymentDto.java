package com.example.backend.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentDto {
    private String paymentKey;
    private Long reservationId;
    private Integer amount; 
    private String orderId;

    private Long userCouponId;
}