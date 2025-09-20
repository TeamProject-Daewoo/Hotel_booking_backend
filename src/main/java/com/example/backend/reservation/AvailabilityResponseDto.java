package com.example.backend.reservation;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailabilityResponseDto {
    private Map<LocalDate, Map<Long, Integer>> availability;
}