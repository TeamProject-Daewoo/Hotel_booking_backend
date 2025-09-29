package com.example.backend.point;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointHistoryDto {
    private final Long id;
    private final int points;
    private final String type; // "EARNED" or "USED"
    private final String description;
    private final LocalDateTime transactionDate;

    @Builder
    public PointHistoryDto(Long id, int points, String type, String description, LocalDateTime transactionDate) {
        this.id = id;
        this.points = points;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}