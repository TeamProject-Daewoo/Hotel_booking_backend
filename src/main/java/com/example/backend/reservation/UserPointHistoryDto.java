package com.example.backend.reservation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserPointHistoryDto {
    private Long reservationId;
    private LocalDate date; // 체크인 날짜로 사용
    private String hotelName;
    private int usedPoints;
    private String type; // "used", "earned", "expired"
}

