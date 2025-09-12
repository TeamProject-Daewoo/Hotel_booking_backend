package com.example.backend.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
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
