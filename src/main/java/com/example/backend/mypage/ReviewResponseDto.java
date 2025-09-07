package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private String hotelId;
    private String hotelName;
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;
}