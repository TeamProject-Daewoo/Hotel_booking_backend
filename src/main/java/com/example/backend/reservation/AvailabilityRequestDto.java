package com.example.backend.reservation;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityRequestDto {
    private String contentId;
    private LocalDate startDate;
    private LocalDate endDate;
}