package com.example.backend.reservation;

import com.example.backend.api.Hotels;
import com.example.backend.api2.Detail;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder // DTO 생성을 위해 @Builder를 추가합니다.
public class ReservationDto {

    // --- 기존 필드 ---
    private Long reservationId;  // ✅ 꼭 필요!
    private final String userId;
    private final String hotelName;
    private final String customerName;
    private final Integer totalPrice;
    private final String status;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final Integer discountPrice;
    private final String phone;

    // --- RoomDetailView를 위해 추가된 필드 ---
    private final Integer basePrice;
    private final HotelDto hotel; // 호텔 상세 정보를 담을 객체
    private final RoomDto room;   // 객실 상세 정보를 담을 객체

    // 호텔 정보를 담기 위한 내부 DTO
    @Getter
    @Builder
    public static class HotelDto {
        private String contentid;
        private String title;
        private String firstimage;
        private String firstimage2;
    }

    // 객실 정보를 담기 위한 내부 DTO
    @Getter
    @Builder
    public static class RoomDto {
        private Long id;
        private String roomtitle;
        private String roomintro;
    }

    // Reservation 엔티티와 Detail(객실) 엔티티를 조합해 DTO를 만드는 static factory method
    public static ReservationDto from(Reservation reservation, Detail roomDetail) {

        HotelDto hotelDto = HotelDto.builder()
                .contentid(reservation.getHotel().getContentid())
                .title(reservation.getHotel().getTitle())
                .firstimage(reservation.getHotel().getFirstimage())
                .build();

        RoomDto roomDto = RoomDto.builder()
                .id(roomDetail.getId())
                .roomtitle(roomDetail.getRoomtitle())
                .roomintro(roomDetail.getRoomintro())
                .build();

        return ReservationDto.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUser() != null ? reservation.getUser().getUsername() : null)
                .hotelName(reservation.getHotel().getTitle())
                .customerName(reservation.getReservName())
                .discountPrice(reservation.getDiscountPrice())
                .phone(reservation.getReservPhone())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .basePrice(reservation.getBasePrice()) // 추가했던 basePrice
                .hotel(hotelDto)
                .room(roomDto)
                .build();
    }
}