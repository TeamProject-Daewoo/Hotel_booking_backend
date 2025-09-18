package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDto {
    private String hotelId;
    private String hotelName;
    private String address;
    private String imageUrl;
    public LikeResponseDto(String hotelId, String hotelName, String address, String imageUrl) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.address = address;
        this.imageUrl = imageUrl;
    }
}
