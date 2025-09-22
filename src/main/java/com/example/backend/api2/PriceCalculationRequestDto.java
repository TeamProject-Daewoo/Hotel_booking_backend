package com.example.backend.api2;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceCalculationRequestDto {
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}