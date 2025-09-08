package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BookingResponseDto {
    private Long reservationId;
    private String hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private Integer totalPrice;
    private Integer numAdults;
    private Integer numChildren;
}
