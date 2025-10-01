package com.example.backend.point;

import com.example.backend.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryDto {

    private final Long id;
    private final int points;
    private final String type;
    private final String description;
    private final LocalDateTime transactionDate;
    // [추가] 호텔 이름을 담을 필드
    private final String hotelName;

    public static PointHistoryDto fromEntity(PointHistory entity) {
        String hotelName = null;
        String description = entity.getDescription();
        Reservation reservation = entity.getReservation();
        if (reservation != null && reservation.getHotel() != null) {
            hotelName = reservation.getHotel().getTitle();
        }

        return PointHistoryDto.builder()
                .id(entity.getId())
                .points(entity.getPoints())
                .type(entity.getType().name())
                .description(description)
                .transactionDate(entity.getTransactionDate())
                .hotelName(hotelName)
                .build();
    }
}

