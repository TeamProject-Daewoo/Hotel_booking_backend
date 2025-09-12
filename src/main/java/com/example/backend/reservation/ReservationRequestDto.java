package com.example.backend.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationRequestDto {
    private String contentid;
    private String roomcode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numAdults;
    private Integer numChildren;
    private Integer totalPrice;
}