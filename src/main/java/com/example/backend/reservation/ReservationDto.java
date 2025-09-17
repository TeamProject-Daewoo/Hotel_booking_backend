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
        
        // 회원/비회원 모두 예약 시 입력한 이름을 사용하도록 변경
        this.customerName = reservation.getReservName(); 

        if (reservation.getUser() != null) {
            this.userId = reservation.getUser().getUsername();
        } else {
            this.userId = null; // 비회원은 userId가 없음
        }

        if (reservation.getHotel() != null) {
            this.hotelName = reservation.getHotel().getTitle();
        } else {
            this.hotelName = "숙소 정보 없음";
        }
    }
}