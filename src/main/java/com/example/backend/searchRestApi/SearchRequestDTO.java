package com.example.backend.searchRestApi;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class SearchRequestDto {
    private String keyword;
    private Date checkInDate;
    private Date checkOutDate;
    private int roomCount;
    private int guestCount;
    private int minPrice;
    private int maxPrice;
    private int rating;
    private Map<String, Boolean> freebies;
    private Map<String, Boolean> amenities;
    private String order;
    private String place;
}
