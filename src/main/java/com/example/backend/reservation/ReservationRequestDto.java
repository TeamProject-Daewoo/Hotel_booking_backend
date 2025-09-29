package com.example.backend.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ReservationRequestDto {
    private Long reservationId;
    private String contentid;
    private String roomcode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numAdults;
    private Integer numChildren;
    private Integer totalPrice;
    private Integer basePrice;
    private Integer discountPrice;
    private String guestName;
    private String phone;
    private Integer usedPoints;
}