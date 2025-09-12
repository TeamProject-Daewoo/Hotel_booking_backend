package com.example.backend.reservation;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ReservationDto {

    private final Long reservationId;
    private final String userId;
    private final String hotelName;
    private final String customerName;
    private final String customerEmail;
    private final Integer totalPrice;
    private final String status;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    @Builder
    public ReservationDto(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.totalPrice = reservation.getTotalPrice();
        this.status = reservation.getStatus();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();

        if (reservation.getUser() != null) {
            this.userId = reservation.getUser().getUsername();
            this.customerName = reservation.getUser().getName();
            this.customerEmail = reservation.getUser().getEmail();
        } else {
            this.userId = null;
            this.customerName = "비회원";
            this.customerEmail = null;
        }

        if (reservation.getHotel() != null) {
            this.hotelName = reservation.getHotel().getTitle();
        } else {
            this.hotelName = "숙소 정보 없음";
        }
    }
}