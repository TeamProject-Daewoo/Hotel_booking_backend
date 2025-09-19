package com.example.backend.wish;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishResponseDto {
    private String contentId;
    private String hotelName;
    // private double rating;
    // private String reviewCount;
    private String address;
    private String imageUrl;
}
