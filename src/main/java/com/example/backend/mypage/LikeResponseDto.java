package com.example.backend.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponseDto {
    private String hotelName;
    // private double rating;
    // private String reviewCount;
    private String address;
    private String imageUrl;
}
