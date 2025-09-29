package com.example.backend.searchRestApi;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class BulkAvailabilityRequestDto {
    private List<String> contentIds;
    private LocalDate startDate;
    private LocalDate endDate;
}
