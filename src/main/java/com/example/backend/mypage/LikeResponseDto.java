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
}
